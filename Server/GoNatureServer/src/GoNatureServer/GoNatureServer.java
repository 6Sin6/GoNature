// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 
package GoNatureServer;

import CommonServer.ocsf.AbstractServer;
import CommonServer.ocsf.ConnectionToClient;
import DataBase.DBConnection;
import Entities.*;
import ServerUIPageController.ServerUIFrameController;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


/**
 * This class overrides some of the methods in the abstract
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */

public class GoNatureServer extends AbstractServer {
    //Class variables *************************************************

    /**
     * The instance of the GoNatureServer.
     */
    private static GoNatureServer server;

    /**
     * The controller for the server user interface.
     */
    private ServerUIFrameController controller;

    /**
     * The connection to the database.
     */
    private DBConnection db;

    /**
     * A map of the signed in instances.
     */
    private Map<String, ConnectionToClient> signedInInstances = new ConcurrentHashMap<>();

    /**
     * A flag indicating whether the server is connected.
     */
    private boolean connected = true;


    //Constructors ****************************************************

    /**
     * Constructs an instance of the GoNatureServer.
     *
     * @param port The port number to connect on.
     * @param controller The controller for the server user interface.
     */
    private GoNatureServer(int port, ServerUIFrameController controller) throws Exception {
        super(port);
        this.controller = controller;
    }

    /**
     * Initializes the database connection.
     *
     * @param controller The controller for the server user interface.
     * @throws Exception If an error occurs while initializing the database connection.
     */
    public void initializeDBConnection(ServerUIFrameController controller) throws Exception {
        try {
            db = DBConnection.getInstance(controller);
        } catch (ClassNotFoundException | SQLException e) {
            controller.addtolog(e.getMessage());
            db = null;
            controller.addtolog("Server failed to initialize");
            connected = false;
        }
    }

    /**
     * Returns the database connection.
     *
     * @return The database connection.
     */
    public DBConnection getDBConnection() {
        return this.db;
    }

    /**
     * Returns the instance of the GoNatureServer.
     *
     * @param port The port number to connect on.
     * @param controller The controller for the server user interface.
     * @return The instance of the GoNatureServer.
     * @throws Exception If an error occurs while getting the instance of the GoNatureServer.
     */
    public static GoNatureServer getInstance(int port, ServerUIFrameController controller) throws Exception {
        if (server == null) {
            server = new GoNatureServer(port, controller);
        }
        return server;
    }

    //Instance methods ************************************************

    /**
     * This method is called when the server starts listening for connections.
     * It starts the client processing thread and the workers.
     */
    protected void serverStarted() {
        if (!connected) {
            try {
                closeServer();
                return;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        server.controller.toggleControllers(true);
        try {
            this.controller.addtolog("Server listening for connections on port " + getPort());
            Workers.startClientProcessingThread(controller, this);
            Workers.SendReminderDayBeforeWorker(db, controller);
            Workers.CancelOrdersThatDidntConfirmWorker(db, controller);
            Workers.enterOrdersFromWaitList48HoursBeforeWorker(db, controller);
            Workers.changeToAbsentOrders(db, controller);
            server.controller.toggleControllers(true);
        } catch (Exception e) {
            controller.addtolog("Error occured on Server : " + e.getMessage());
        }
    }


    /**
     * This method handles any messages received from the client.
     * It checks the type of the message and calls the appropriate handler method based on the opcode of the message.
     * If an exception occurs while handling the message, it sends an error message back to the client.
     *
     * @param msg The message received from the client. It should be an instance of the Message class.
     * @param client The connection from which the message originated.
     */
    public void handleMessageFromClient(Object msg, ConnectionToClient client) {
        if (!(msg instanceof Message)) {
            return;
        }
        Message message = (Message) msg;
        try {
            switch (message.getMsgOpcode()) {
                case OP_SYNC_HANDSHAKE:
                    handleSyncHandshake(message, client);
                    break;
                case OP_LOGOUT:
                    handleLogout(message, client);
                    break;
                case OP_SIGN_IN:
                    handleSignIn(message, client);
                    break;
                case OP_GET_VISITOR_ORDERS:
                    handleGetVisitorOrders(message, client);
                    break;
                case OP_GET_ORDER_BY_ID:
                    handleGetOrderByID(message, client);
                    break;
                case OP_UPDATE_ORDER_DETAILS_BY_ORDERID:
                    handleUpdateOrderDetailsByOrderId(message, client);
                    break;
                case OP_CREATE_NEW_VISITATION:
                    handleCreateNewVisitation(message, client);
                    break;
                case OP_GET_USER_ORDERS_BY_USERID_ORDERID:
                    handleGetUserOrdersByUserID(message, client);
                    break;
                case OP_ACTIVATE_GROUP_GUIDE:
                    handleActivateGroupGuide(message, client);
                    break;
                case OP_HANDLE_VISITATION_CANCEL_ORDER:
                    handleCancelOrderVisitation(message, client);
                    break;
                case OP_GET_REQUESTS_FROM_PARK_MANAGER:
                    handleGetRequestsFromParkManager(message, client);
                    break;
                case OP_AUTHORIZE_PARK_REQUEST:
                    handleAuthorizeParkRequest(message, client);
                    break;
                case OP_DECLINE_PARK_REQUEST:
                    handleDeclineParkRequest(message, client);
                    break;
                case OP_SUBMIT_REQUESTS_TO_DEPARTMENT:
                    handleSubmitRequestsToDepartment(message, client);
                    break;
                case OP_GET_PARK_DETAILS_BY_PARK_ID:
                    handleGetParkDetailsByParkID(message, client);
                    break;
                case OP_MARK_ORDER_AS_PAID:
                    handleMarkOrderAsPaid(message, client);
                    break;
                case OP_UPDATE_EXIT_TIME_OF_ORDER:
                    handleUpdateExitTimeOfOrder(message, client);
                    break;
                case OP_VIEW_REPORT_BLOB:
                    handleViewReportBlob(message, client);
                    break;
                case OP_GENERATE_REPORT_BLOB:
                    handleGenerateReportBlob(message, client);
                    break;
                case OP_GET_AVAILABLE_SPOTS:
                    handleGetAvailableSpots(message, client);
                    break;
                case OP_INSERT_VISITATION_TO_WAITLIST:
                    handleCreateNewVisitationForWaitList(message, client);
                    break;
                case OP_CONFIRMATION:
                    handleConfirmOrderVisitation(message, client);
                    break;
                case OP_CHECK_AVAILABLE_SPOT:
                    handleCheckAvailableSpot(message, client);
                    break;
                case OP_CREATE_SPOTANEOUS_ORDER:
                    handleCreateSpotaneousOrder(message, client);
                    break;
                case OP_GET_PARK_NAME_BY_PARK_ID:
                    handleGetParkNameByParkID(message, client);
                    break;
                case OP_GET_PARKS_BY_DEPARTMENT:
                    handleGetParksByDepartment(message, client);
                    break;
                case OP_GET_DEPARTMENT_MANAGER_PARKS:
                    handleGetDepartmentParkNames(message, client);
                    break;
                case OP_MARK_GROUP_GUIDE_ORDER_AS_PAID:
                    handleMarkGroupGuideOrderAsPaid(message, client);
                    break;
                case OP_ENTER_VISITORS_TO_PARK:
                    handleEnterVisitorsToPark(message, client);
                    break;
                case OP_QUIT:
                    handleQuit(client);
                    break;
                default:
                    controller.addtolog("Error Unknown Opcode");
            }
        } catch (SQLException e) {
            Message respondMsg = new Message(OpCodes.OP_DB_ERR, message.getMsgUserName(), null);
            try {
                client.sendToClient(respondMsg);
            } catch (IOException ex) {
                controller.addtolog("Failed to send message to client");
            }
        } catch (IOException e) {
            controller.addtolog("Failed to send message to client");
        } catch (Exception e) {
            controller.addtolog("Error occured on Server : " + e.getMessage());
        }
    }

    /**
     * Handles the retrieval of parks by department ID.
     * The method retrieves the parks from the database based on the department ID received from the client.
     * The result is then sent back to the client.
     *
     * @param message The message received from the client, containing the department ID.
     * @param client The connection from which the message originated.
     * @throws SQLException If an error occurs while retrieving the parks from the database.
     * @throws IOException If an error occurs while sending the message to the client.
     */
    private void handleGetParksByDepartment(Message message, ConnectionToClient client) throws SQLException, IOException {
        String departmentID = (String) message.getMsgData();
        Map<String, String> parks = db.getParksByDepartment(Integer.parseInt(departmentID));
        Message respondMsg = new Message(OpCodes.OP_GET_PARKS_BY_DEPARTMENT, message.getMsgUserName(), parks);

        client.sendToClient(respondMsg);
    }

    /**
     * Handles the retrieval of a park name by park ID.
     * The method retrieves the park name from the database based on the park ID received from the client.
     * The result is then sent back to the client.
     *
     * @param message The message received from the client, containing the park ID.
     * @param client The connection from which the message originated.
     * @throws IOException If an error occurs while sending the message to the client.
     */
    private void handleGetParkNameByParkID(Message message, ConnectionToClient client) throws IOException {
        String parkID = (String) message.getMsgData();
        String parkName = db.getParkNameByID(Integer.parseInt(parkID));
        Message respondMsg = new Message(OpCodes.OP_GET_PARK_NAME_BY_PARK_ID, message.getMsgUserName(), parkName);

        client.sendToClient(respondMsg);
    }

    /**
     * Handles the entry of visitors to a park.
     * The method sets the enter time of an order in the database based on the order data received from the client.
     * The result is then sent back to the client.
     *
     * @param message The message received from the client, containing the order data.
     * @param client The connection from which the message originated.
     * @throws Exception If an error occurs while setting the enter time of the order.
     */
    private void handleEnterVisitorsToPark(Message message, ConnectionToClient client) throws Exception {
        Order order = (Order) message.getMsgData();
        boolean result = db.setEnterTimeOfOrder(order.getOrderID(), order.getOrderStatus());
        Message respondMsg = new Message(OpCodes.OP_ENTER_VISITORS_TO_PARK, message.getMsgUserName(), result);

        client.sendToClient(respondMsg);
    }

    /**
     * Handles the synchronization handshake message from the client.
     *
     * @param message The message received from the client.
     * @param client The connection from which the message originated.
     * @throws IOException If an error occurs while sending the message to the client.
     */
    private void handleSyncHandshake(Message message, ConnectionToClient client) throws IOException {
        client.sendToClient(message);
    }

    /**
     * Handles the logout message from the client.
     *
     * @param message The message received from the client.
     * @param client The connection from which the message originated.
     * @throws IOException If an error occurs while sending the message to the client.
     */
    private void handleLogout(Message message, ConnectionToClient client) throws IOException {
        signedInInstances.remove(message.getMsgUserName());
        client.sendToClient("logged out successfully");
    }

    /**
     * Handles the sign in message from the client.
     * This method is responsible for authenticating the user based on the credentials provided in the message.
     * If the user is already signed in, it sends a message back to the client indicating that the user is already logged in.
     * If the user credentials are invalid, it sends a message back to the client indicating that the sign in was unsuccessful.
     * If the user is a VisitorGroupGuide but is not activated, it sends a message back to the client indicating that the Visitor Group Guide is not activated.
     * If the sign in is successful, it adds the user to the signed in instances and sends a message back to the client with the authenticated user data.
     * If the message data is a string, it checks if the user is a group guide. If the user is a group guide, it sends a message back to the client with the username.
     * If the user is already signed in, it checks if the client is crashed. If the client is crashed, it removes the crashed client from the signed in instances and adds it again with the new client.
     * If the sign in is successful, it adds the user to the signed in instances and sends a message back to the client with the username.
     *
     * @param message The message received from the client. It should be an instance of the Message class and contain the user credentials or username.
     * @param client The connection from which the message originated.
     * @throws Exception If an error occurs while handling the sign in.
     */
    private void handleSignIn(Message message, ConnectionToClient client) throws Exception {
        if (message.getMsgData() instanceof User) {
            User userCredentials = (User) message.getMsgData();
            if (signedInInstances.containsKey(userCredentials.getUsername())) {
                if (signedInInstances.get(userCredentials.getUsername()).getInetAddress() == null) {
                    signedInInstances.remove(userCredentials.getUsername());
                } else {
                    Message respondMsg = new Message(OpCodes.OP_SIGN_IN_ALREADY_LOGGED_IN, userCredentials.getUsername(), null);
                    client.sendToClient(respondMsg);
                    return;
                }
            }
            User authenticatedUser = db.login(userCredentials.getUsername(), userCredentials.getPassword());
            if (authenticatedUser.getRole() == Role.ROLE_GUEST) { // Invalid username or password case...
                Message respondMsg = new Message(OpCodes.OP_SIGN_IN, "", authenticatedUser);
                client.sendToClient(respondMsg);
                return;
            }
            if (authenticatedUser instanceof VisitorGroupGuide && (Objects.equals(authenticatedUser.getUsername(), ""))) {
                Message respondMsg = new Message(OpCodes.OP_SIGN_IN, "", "Visitor Group Guide is not activated");
                client.sendToClient(respondMsg);
                return;
            }
            signedInInstances.put(authenticatedUser.getUsername(), client);
            Message respondMsg = new Message(OpCodes.OP_SIGN_IN, authenticatedUser.getUsername(), authenticatedUser);
            client.sendToClient(respondMsg);
        }
        if (message.getMsgData() instanceof String) {
            String username = (String) message.getMsgData();
            if (db.isGroupGuide(username)) {
                Message respondMsg = new Message(OpCodes.OP_SIGN_IN_VISITOR_GROUP_GUIDE, username, username);
                client.sendToClient(respondMsg);
                return;
            }
            if (signedInInstances.containsKey(username)) {
                if (signedInInstances.get(username).getInetAddress() == null) {
                    // remove crashed client from the map and add it again with the new client.
                    signedInInstances.remove(username);
                    signedInInstances.put(username, client);
                    Message respondMsg = new Message(OpCodes.OP_SIGN_IN, username, username);
                    client.sendToClient(respondMsg);
                } else {
                    Message respondMsg = new Message(OpCodes.OP_SIGN_IN_ALREADY_LOGGED_IN, username, null);
                    client.sendToClient(respondMsg);
                }
            } else {
                signedInInstances.put(username, client);
                Message respondMsg = new Message(OpCodes.OP_SIGN_IN, username, username);
                client.sendToClient(respondMsg);
            }
        }

    }

    /**
     * Handles the creation of a spontaneous order.
     * A spontaneous order is created when a visitor arrives at the park without a pre-existing order.
     * The method sets the visitation date and entry time to the current time, and calculates the exit time based on the expected visit duration.
     * The order is then added to the database.
     *
     * @param message The message received from the client, containing the order data.
     * @param client The connection from which the message originated.
     * @throws Exception If an error occurs while creating the order.
     */
    private void handleCreateSpotaneousOrder(Message message, ConnectionToClient client) throws Exception {
        Order order = (Order) message.getMsgData();
        order.setVisitationDate(new Timestamp(System.currentTimeMillis()));
        order.setEnteredTime(new Timestamp(System.currentTimeMillis()));
        order.setExitedTime(createExitTime(order.getEnteredTime(), db.getExpectedTime(order.getParkID())));
        Order newOrder = db.addOrder(order);
        Message createOrderMsg;
        if (order != null) {
            createOrderMsg = new Message(OpCodes.OP_CREATE_SPOTANEOUS_ORDER, message.getMsgUserName(), newOrder);
        } else {
            createOrderMsg = new Message(OpCodes.OP_DB_ERR);
        }
        client.sendToClient(createOrderMsg);
    }

    /**
     * Handles the request to check the available spots in a park.
     * The method retrieves the park details and calculates the number of available spots based on the park's capacity and the number of visitors before and after the current time.
     * The result is then sent back to the client.
     *
     * @param message The message received from the client, containing the park ID.
     * @param client The connection from which the message originated.
     * @throws Exception If an error occurs while checking the available spots.
     */
    private void handleCheckAvailableSpot(Message message, ConnectionToClient client) throws Exception {
        String parkID = (String) message.getMsgData();
        Park park = db.getParkDetails(parkID);
        if (park == null) {
            Message respondMsg = new Message(OpCodes.OP_DB_ERR, message.getMsgUserName(), false);
            client.sendToClient(respondMsg);
            return;
        }
        Integer parkCapacity = park.getCapacity();
        Integer parkEpectedVisitationTime = db.getExpectedTime(parkID);
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        Integer VisitorsBefore = db.GetAvailableSpotForEntry(parkID, currentTime);
        Integer VisitorsAfter = db.GetAvailableSpotForEntry(parkID, createExitTime(currentTime, parkEpectedVisitationTime));

        Integer availableSpots = Math.max(parkCapacity - Math.max(VisitorsBefore, VisitorsAfter), 0);
        ArrayList<Integer> availableSpotsList = new ArrayList<>();
        availableSpotsList.add(availableSpots);
        availableSpotsList.add(parkCapacity);
        Message respondMsg = new Message(OpCodes.OP_CHECK_AVAILABLE_SPOT, message.getMsgUserName(), availableSpotsList);
        client.sendToClient(respondMsg);
    }


    /**
     * Handles the retrieval of a visitor's orders.
     * The method retrieves the orders of the visitor from the database and sends them back to the client.
     *
     * @param message The message received from the client, containing the visitor data.
     * @param client The connection from which the message originated.
     * @throws Exception If an error occurs while retrieving the orders.
     */
    private void handleGetVisitorOrders(Message message, ConnectionToClient client) throws Exception {
        User visitor = (User) message.getMsgData();
        ArrayList<Order> requestedOrders;
        if (visitor instanceof AbstractVisitor) {
            requestedOrders = db.getUserOrders(((AbstractVisitor) visitor).getID());
        } else {
            requestedOrders = db.getUserOrders(((SingleVisitor) visitor).getID());
        }
        Message getVisitorOrdersMsg = new Message(OpCodes.OP_GET_VISITOR_ORDERS, visitor.getUsername(), requestedOrders);
        client.sendToClient(getVisitorOrdersMsg);
    }

    /**
     * Handles the creation of a new visitation order.
     * This method is responsible for creating a new visitation order based on the order data provided in the message.
     * It first checks if the message data is an instance of the Order class. If not, it sends an error message back to the client.
     * It then attempts to extract the order from the wait list. If this fails, it sends an error message back to the client.
     * It sets the exit time of the order based on the entered time and the expected time retrieved from the database.
     * It checks if an order with the same visitor ID, park ID, and visitation date already exists in the database. If such an order exists, it sends a message back to the client indicating that the order already exists.
     * It checks if there is availability before and after the reservation time. If there is availability, it sets the order status to accepted and adds the order to the database.
     * If the order is successfully added to the database, it sends a message back to the client with the new order and sends an email to the client's email address indicating that the order has been created successfully.
     * If the order is not successfully added to the database, it sends an error message back to the client.
     * If there is no availability before or after the reservation time, it sends a message back to the client indicating that there are no available spots.
     *
     * @param message The message received from the client. It should be an instance of the Message class and contain the order data.
     * @param client The connection from which the message originated.
     * @throws Exception If an error occurs while creating the new visitation order.
     */
    private void handleCreateNewVisitation(Message message, ConnectionToClient client) throws Exception {
        if (!(message.getMsgData() instanceof Order)) {
            Message respondMsg = new Message(OpCodes.OP_DB_ERR, message.getMsgUserName(), null);
            client.sendToClient(respondMsg);
        }
        Order order = (Order) message.getMsgData();
        if (!db.extractFromWaitList(new Order(null, order.getParkID(), null,
                null, null, null, order.getEnteredTime(),
                null, null, null, 0))) {
            Message respondMsg = new Message(OpCodes.OP_DB_ERR, null, null);
            client.sendToClient(respondMsg);
        }
        order.setExitedTime(createExitTime(order.getEnteredTime(), db.getExpectedTime(order.getParkID())));
        if (db.checkOrderExists(order.getVisitorID(), order.getParkID(), order.getVisitationDate())) {
            Message createOrderMsg = new Message(OpCodes.OP_ORDER_ALREADY_EXIST);
            client.sendToClient(createOrderMsg);
            return;
        }
        if (db.CheckAvailabilityBeforeReservationTime(order) && db.CheckAvailabilityAfterReservationTime(order)) {
            order.setOrderStatus(OrderStatus.STATUS_ACCEPTED);
            Order newOrder = db.addOrder(order);
            if (newOrder != null) {
                Message createOrderMsg = new Message(OpCodes.OP_CREATE_NEW_VISITATION, message.getMsgUserName(), newOrder);
                client.sendToClient(createOrderMsg);
                new Thread(() -> {
                    GmailSender.sendEmail(newOrder.getClientEmailAddress(), "Your order " + order.getOrderID() + " created", "Your order " + order.getOrderID() + " " + order.getVisitationDate().toString() + " created Successfully");
                }).start();
            } else {
                Message respondMsg = new Message(OpCodes.OP_DB_ERR, message.getMsgUserName(), null);
                client.sendToClient(respondMsg);
            }

        } else {
            Message NO_AVAILABLE_SPOT = new Message(OpCodes.OP_NO_AVAILABLE_SPOT, message.getMsgUserName(), null);
            client.sendToClient(NO_AVAILABLE_SPOT);
        }

    }

    /**
     * Handles the retrieval of user orders by user ID.
     * This method retrieves the orders of a user from the database based on the user ID provided in the message.
     * It first checks if the user is a group guide. If the user is a group guide, it sends a message back to the client indicating that the user is a group guide.
     * It then retrieves the user order from the database based on the user ID and order ID provided in the message.
     * The result is then sent back to the client.
     *
     * @param message The message received from the client. It should be an instance of the Message class and contain the user ID and order ID.
     * @param client The connection from which the message originated.
     * @throws Exception If an error occurs while retrieving the user orders.
     */
    private void handleGetUserOrdersByUserID(Message message, ConnectionToClient client) throws Exception {
        String[] data = (String[]) message.getMsgData();
        if (db.isGroupGuide(data[0])) {
            Message respondMsg = new Message(OpCodes.OP_SIGN_IN_VISITOR_GROUP_GUIDE, "", null);
            client.sendToClient(respondMsg);
            return;
        }
        Order userOrder = db.getUserOrderByUserID(data[0], data[1]);
        Message getUserMsg = new Message(OpCodes.OP_GET_USER_ORDERS_BY_USERID_ORDERID, "", userOrder);
        client.sendToClient(getUserMsg);
    }

    /**
     * Handles the activation of a group guide.
     *
     * @param message The message received from the client.
     * @param client The connection from which the message originated.
     * @throws Exception If an error occurs while activating the group guide.
     */
    private void handleActivateGroupGuide(Message message, ConnectionToClient client) throws Exception {
        String groupGuideID = (String) message.getMsgData();
        String retVal = db.activateGroupGuide(groupGuideID);
        Message registerGroupGuideMessage = new Message(OpCodes.OP_ACTIVATE_GROUP_GUIDE, null, retVal);
        client.sendToClient(registerGroupGuideMessage);
    }

    /**
     * Handles the retrieval of requests from the park manager.
     *
     * @param message The message received from the client.
     * @param client The connection from which the message originated.
     * @throws Exception If an error occurs while retrieving the requests.
     */
    private void handleGetRequestsFromParkManager(Message message, ConnectionToClient client) throws Exception {
        Integer departmentID = (Integer) message.getMsgData();
        ArrayList<RequestChangingParkParameters> requests = db.getRequestsFromParkManager(departmentID);
        Message retrieveRequestsMsg = new Message(OpCodes.OP_GET_REQUESTS_FROM_PARK_MANAGER, message.getMsgUserName(), requests);
        client.sendToClient(retrieveRequestsMsg);
    }

    /**
     * Handles the authorization of a park request.
     *
     * @param message The message received from the client.
     * @param client The connection from which the message originated.
     * @throws Exception If an error occurs while authorizing the park request.
     */
    private void handleAuthorizeParkRequest(Message message, ConnectionToClient client) throws Exception {
        RequestChangingParkParameters authRequest = (RequestChangingParkParameters) message.getMsgData();
        boolean isAuthorized = db.authorizeParkRequest(authRequest);
        Message authorizeRequestMsg = new Message(OpCodes.OP_AUTHORIZE_PARK_REQUEST, message.getMsgUserName(), isAuthorized);
        client.sendToClient(authorizeRequestMsg);
    }

    /**
     * Handles the decline of a park request.
     *
     * @param message The message received from the client.
     * @param client The connection from which the message originated.
     * @throws Exception If an error occurs while declining the park request.
     */
    private void handleDeclineParkRequest(Message message, ConnectionToClient client) throws Exception {
        RequestChangingParkParameters unauthRequest = (RequestChangingParkParameters) message.getMsgData();
        boolean isUnauthorized = db.unauthorizeParkRequest(unauthRequest);
        Message unauthorizeRequestMsg = new Message(OpCodes.OP_DECLINE_PARK_REQUEST, message.getMsgUserName(), isUnauthorized);
        client.sendToClient(unauthorizeRequestMsg);
    }

    /**
     * Handles the submission of requests to the department.
     *
     * @param message The message received from the client.
     * @param client The connection from which the message originated.
     * @throws Exception If an error occurs while submitting the requests.
     */
    private void handleSubmitRequestsToDepartment(Message message, ConnectionToClient client) throws Exception {
        Map<ParkParameters, RequestChangingParkParameters> requestMap = (Map<ParkParameters, RequestChangingParkParameters>) message.getMsgData();
        boolean isSubmitted = db.submitRequestsToDepartment(requestMap);
        Message submitRequestMsg = new Message(OpCodes.OP_SUBMIT_REQUESTS_TO_DEPARTMENT, message.getMsgUserName(), isSubmitted);
        client.sendToClient(submitRequestMsg);
    }

    /**
     * Handles the retrieval of park details by park ID.
     *
     * @param message The message received from the client.
     * @param client The connection from which the message originated.
     * @throws Exception If an error occurs while retrieving the park details.
     */
    private void handleGetParkDetailsByParkID(Message message, ConnectionToClient client) throws Exception {
        String ParkID = (String) message.getMsgData();
        Park park = db.getParkDetails(ParkID);
        Message submitRequestMsg = new Message(OpCodes.OP_GET_PARK_DETAILS_BY_PARK_ID, message.getMsgUserName(), park);
        client.sendToClient(submitRequestMsg);
    }

    /**
     * Handles the quit message from the client.
     *
     * @param client The connection from which the message originated.
     * @throws Exception If an error occurs while handling the quit message.
     */
    private void handleQuit(ConnectionToClient client) throws Exception {
        signedInInstances.values().removeIf(value -> value == client);
        controller.addtolog("Client " + client + " Disconnected");
        controller.removeRowByIP(client.getInetAddress().getHostAddress());
        client.close();
    }

    /**
     * Handles the cancellation of a visitation order.
     *
     * @param message The message received from the client.
     * @param client The connection from which the message originated.
     * @throws Exception If an error occurs while cancelling the visitation order.
     */
    private void handleCancelOrderVisitation(Message message, ConnectionToClient client) throws Exception {
        Order order = (Order) message.getMsgData();
        if (!db.extractFromWaitList(order)) {
            Message respondMsg = new Message(OpCodes.OP_DB_ERR, null, null);
            client.sendToClient(respondMsg);
        }
        boolean isCanceled = db.updateOrderStatusAsCancelled(order);
        if (!isCanceled) {
            Message respondMsg = new Message(OpCodes.OP_DB_ERR, null, null);
            client.sendToClient(respondMsg);
        }
        Message respondMsg = new Message(OpCodes.OP_HANDLE_VISITATION_CANCEL_ORDER, message.getMsgUserName(), isCanceled);
        client.sendToClient(respondMsg);
    }

    /**
     * This method handles the confirmation of a visitation order.
     * It first checks if the order has been paid for. If the order has been paid for, it updates the order status to confirmed and paid.
     * If the order has not been paid for, it updates the order status to confirmed and pending payment.
     * If the order status is not successfully updated, it sends an error message back to the client.
     * If the order status is successfully updated, it sends a message back to the client indicating that the order has been confirmed.
     *
     * @param message The message received from the client. It should be an instance of the Message class and contain the order data.
     * @param client The connection from which the message originated.
     * @throws Exception If an error occurs while confirming the visitation order.
     */
    private void handleConfirmOrderVisitation(Message message, ConnectionToClient client) throws Exception {
        Order order = (Order) message.getMsgData();
        String orderID = order.getOrderID();
        boolean isChanged;
        if (db.checkOrderPayment(order)) {
            isChanged = db.updateOrderStatus(orderID, OrderStatus.STATUS_CONFIRMED_PAID);
        } else {
            isChanged = db.updateOrderStatus(orderID, OrderStatus.STATUS_CONFIRMED_PENDING_PAYMENT);
        }
        if (!isChanged) {
            Message respondMsg = new Message(OpCodes.OP_DB_ERR, null, null);
            client.sendToClient(respondMsg);
            return;
        }
        Message respondMsg = new Message(OpCodes.OP_CONFIRMATION, message.getMsgUserName(), isChanged);
        client.sendToClient(respondMsg);
    }

    /**
     * Handles the retrieval of an order by its ID.
     *
     * @param message The message received from the client.
     * @param client The connection from which the message originated.
     * @throws Exception If an error occurs while retrieving the order.
     */
    private void handleGetOrderByID(Message message, ConnectionToClient client) throws Exception {
        String orderID = (String) message.getMsgData();
        Order order = db.getOrderById(orderID);
        Message respondMsg = new Message(OpCodes.OP_GET_ORDER_BY_ID, message.getMsgUserName(), order);
        client.sendToClient(respondMsg);
    }

    /**
     * Handles the update of the exit time of an order.
     *
     * @param message The message received from the client.
     * @param client The connection from which the message originated.
     * @throws Exception If an error occurs while updating the exit time of the order.
     */
    private void handleUpdateExitTimeOfOrder(Message message, ConnectionToClient client) throws Exception {
        String orderID = message.getMsgData().toString();
        String answer = db.setExitTimeOfOrder(orderID);
        Message respondMsg = new Message(OpCodes.OP_UPDATE_EXIT_TIME_OF_ORDER, message.getMsgUserName(), answer);
        client.sendToClient(respondMsg);
    }

    /**
     * Handles the update of order details by order ID.
     *
     * @param message The message received from the client.
     * @param client The connection from which the message originated.
     * @throws Exception If an error occurs while updating the order details.
     */
    private void handleUpdateOrderDetailsByOrderId(Message message, ConnectionToClient client) throws Exception {
        String[] details = (String[]) message.getMsgData();
        boolean isUpdated = db.updateOrderDetails(details);
        Message respondMsg = new Message(OpCodes.OP_UPDATE_ORDER_DETAILS_BY_ORDERID, message.getMsgUserName(), isUpdated);
        client.sendToClient(respondMsg);
    }

    /**
     * Handles the marking of an order as paid.
     *
     * @param message The message received from the client.
     * @param client The connection from which the message originated.
     * @throws Exception If an error occurs while marking the order as paid.
     */
    private void handleMarkOrderAsPaid(Message message, ConnectionToClient client) throws Exception {
        Order order = (Order) message.getMsgData();
        boolean isMarkedAsPaid = db.markOrderAsPaid(order);
        Message respondMsg = new Message(OpCodes.OP_MARK_ORDER_AS_PAID, message.getMsgUserName(), isMarkedAsPaid);
        client.sendToClient(respondMsg);
    }

    /**
     * Handles the viewing of a report blob.
     * The method retrieves the report blob from the database based on the parameters received from the client:
     * (departmentReport boolean, report type String, month String, year String, bodyId [Department ID or Park ID] String).
     * @param message The message received from the client.
     * @param client The connection from which the message originated.
     * @throws Exception If an error occurs while viewing the report blob.
     */
    private void handleViewReportBlob(Message message, ConnectionToClient client) throws Exception {
        String[] params = (String[]) message.getMsgData();
        byte[] pdfBlob = db.getReportBlob(Boolean.parseBoolean(params[0]), params[1], params[2], params[3], params[4]);
        Message respondMsg = new Message(OpCodes.OP_VIEW_REPORT_BLOB, message.getMsgUserName(), pdfBlob);
        client.sendToClient(respondMsg);
    }

    /**
     * Handles the generation of a report blob.
     * This method is responsible for generating a report blob based on the report type provided in the message.
     * The report type can be one of the following: "visitations", "cancellations", "numofvisitors", "usage".
     * If the report type is "visitations" or "cancellations", it retrieves the department ID based on the username provided in the message and generates the corresponding report.
     * If the report type is "numofvisitors" or "usage", it retrieves the park ID based on the username provided in the message and generates the corresponding report.
     * If the report is successfully generated, it sends a message back to the client indicating that the report has been generated.
     * If the report is not successfully generated, it does not send a message back to the client.
     *
     * @param message The message received from the client. It should be an instance of the Message class and contain the report type.
     * @param client The connection from which the message originated.
     * @throws Exception If an error occurs while generating the report blob.
     */
    private void handleGenerateReportBlob(Message message, ConnectionToClient client) throws Exception {
        String reportType = (String) message.getMsgData();
        boolean isGenerated = false;
        String id; // used for department ID (for first 2 cases) or park ID (for other cases)
        switch (reportType) {
            case "visitations":
                id = db.getDepartmentIDByManagerUsername(message.getMsgUserName());
                if (id == null) {
                    break;
                }
                isGenerated = db.generateVisitationReport(id);
                break;
            case "cancellations":
                id = db.getDepartmentIDByManagerUsername(message.getMsgUserName());
                if (id == null) {
                    break;
                }
                isGenerated = db.generateCancellationsReport(id);
                break;
            case "numofvisitors":
                id = db.getParkIDByManagerUsername(message.getMsgUserName());
                if (id != null)
                    isGenerated = db.generateNumOfVisitorsReport(Integer.parseInt(id));
                break;
            case "usage":
                id = db.getParkIDByManagerUsername(message.getMsgUserName());
                if (id != null)
                    isGenerated = db.generateUsageReport(Integer.parseInt(id));
                break;
        }


        Message respondMsg = new Message(OpCodes.OP_GENERATE_REPORT_BLOB, message.getMsgUserName(), isGenerated);
        client.sendToClient(respondMsg);
    }

    /**
     * Handles the retrieval of available spots for a specific order.
     * This method retrieves the available spots for a specific order from the database.
     * It first checks if the list of available spots is empty. If it is, it sends a message back to the client indicating that there are no available spots.
     * If the list of available spots is not empty, it sends a message back to the client with the list of available spots.
     *
     * @param message The message received from the client. It should be an instance of the Message class and contain the order data.
     * @param client The connection from which the message originated.
     * @throws Exception If an error occurs while retrieving the available spots.
     */
    private void handleGetAvailableSpots(Message message, ConnectionToClient client) throws Exception {
        Order order = (Order) message.getMsgData();
        ArrayList<Timestamp> availableSpots = db.getAvailableTimeStamps(order);
        if (availableSpots.isEmpty()) {
            Message respondMsg = new Message(OpCodes.OP_NO_AVAILABLE_SPOT, message.getMsgUserName(), null);
            client.sendToClient(respondMsg);
        }
        Message respondMsg = new Message(OpCodes.OP_GET_AVAILABLE_SPOTS, message.getMsgUserName(), availableSpots);
        client.sendToClient(respondMsg);
    }

    /**
     * Handles the marking of a group guide order as paid.
     *
     * @param message The message received from the client.
     * @param client The connection from which the message originated.
     * @throws Exception If an error occurs while marking the group guide order as paid.
     */
    private void handleMarkGroupGuideOrderAsPaid(Message message, ConnectionToClient client) throws Exception {
        Order order = (Order) message.getMsgData();
        boolean isMarkedAsPaid = db.updateOrderStatus(order.getOrderID(), OrderStatus.STATUS_CONFIRMED_PAID);
        Message respondMsg = new Message(OpCodes.OP_MARK_GROUP_GUIDE_ORDER_AS_PAID, message.getMsgUserName(), isMarkedAsPaid);
        client.sendToClient(respondMsg);
    }

    /**
     * Creates an exit time for a visitation order.
     *
     * @param enterTime The enter time of the visitation order.
     * @param expectedTime The expected time of the visitation order.
     * @return The created exit time.
     */
    public static Timestamp createExitTime(Timestamp enterTime, int expectedTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(enterTime.getTime());
        cal.add(Calendar.MINUTE, expectedTime);
        return (new Timestamp(cal.getTimeInMillis()));
    }

    /**
     * This method handles the creation of a new visitation order for the wait list.
     * It first checks if an order with the same visitor ID, park ID, and visitation date already exists in the database.
     * If such an order exists, it sends a message back to the client indicating that the order already exists.
     * If no such order exists, it sets the order status to waitlist and adds the order to the database.
     * If the order is successfully added to the database, it sends a message back to the client with the new order and sends an email to the client's email address indicating that the order has been added to the waitlist.
     * If the order is not successfully added to the database, it sends an error message back to the client.
     *
     * @param message The message received from the client. It should be an instance of the Message class and contain the order data.
     * @param client The connection from which the message originated.
     * @throws Exception If an error occurs while creating the new visitation order for the wait list.
     */
    private void handleCreateNewVisitationForWaitList(Message message, ConnectionToClient client) throws Exception {
        Order order = (Order) message.getMsgData();
        order.setExitedTime(createExitTime(order.getEnteredTime(), db.getExpectedTime(order.getParkID())));
        if (db.checkOrderExists(order.getVisitorID(), order.getParkID(), order.getVisitationDate())) {
            Message createOrderMsg = new Message(OpCodes.OP_ORDER_ALREADY_EXIST);
            client.sendToClient(createOrderMsg);
            return;
        }
        order.setOrderStatus(OrderStatus.STATUS_WAITLIST);
        Order newOrder = db.addOrder(order);
        if (newOrder != null) {
            Message createOrderMsg = new Message(OpCodes.OP_INSERT_VISITATION_TO_WAITLIST, message.getMsgUserName(), newOrder);
            client.sendToClient(createOrderMsg);
            new Thread(() -> {
                GmailSender.sendEmail(newOrder.getClientEmailAddress(), "Your order " + order.getOrderID() + " entered the waitlist", "Your order " + order.getOrderID() + " " + order.getVisitationDate().toString() + " joined the waitlist");
            }).start();
        } else {
            Message respondMsg = new Message(OpCodes.OP_DB_ERR, message.getMsgUserName(), null);
            client.sendToClient(respondMsg);
        }
    }

    /**
     * Handles the retrieval of department park names.
     *
     * @param message The message received from the client.
     * @param client The connection from which the message originated.
     * @throws Exception If an error occurs while retrieving the department park names.
     */
    private void handleGetDepartmentParkNames(Message message, ConnectionToClient client) throws Exception {
        Integer departmentID = (Integer) message.getMsgData();
        ArrayList<String> parkNames = db.getDepartmentParkNames(departmentID);
        Message respondMsg = new Message(OpCodes.OP_GET_DEPARTMENT_MANAGER_PARKS, message.getMsgUserName(), parkNames);
        client.sendToClient(respondMsg);
    }

    /**
     * This method is called when the server stops listening for connections.
     * It closes the database connection and shuts down the executors.
     */
    @Override
    protected void serverStopped() {
        this.controller.addtolog("Server has stopped listening for connections.");
        if (db != null) {
            db.closeConnection();
        }
        Workers.shutdownExecutors();
    }

    /**
     * This method is called when the server is closed.
     * It toggles the controllers, closes the database connection and shuts down the executors.
     */
    @Override
    protected void serverClosed() {
        controller.toggleControllers(false);
        this.controller.addtolog("Server has closed.");
        if (db != null) {
            db.closeConnection();
        }
        Workers.shutdownExecutors();
    }

    /**
     * Closes the server, just before disconnecting all clients and cleaning up the class instances.
     */
    public static void closeServer() {
        try {
            if (server == null) {
                System.out.println("Server isn't initialized");
                return;
            }
            server.sendToAllClients("Disconnect");
            server.stopListening();
            server.close();
            server.controller.toggleControllers(false);
            server = null;
        } catch (Exception e) {
            server.controller.addtolog("Server isn't initialized");
        }
    }

}
//End of GoNatureServer class
