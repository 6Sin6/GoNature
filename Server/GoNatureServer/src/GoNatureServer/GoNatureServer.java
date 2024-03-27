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
import java.time.LocalDate;
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

    private static GoNatureServer server;
    private ServerUIFrameController controller;
    private DBConnection db;

    private Map<String, ConnectionToClient> signedInInstances = new ConcurrentHashMap<>();

    private boolean connected = true;


    //Constructors ****************************************************

    /**
     * Constructs an instance of the echo server.
     *
     * @param port The port number to connect on.
     */

    private GoNatureServer(int port, ServerUIFrameController controller) throws Exception {
        super(port);
        this.controller = controller;
    }

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

    public DBConnection getDBConnection(ServerUIFrameController controller) throws Exception {
        return this.db;
    }

    public static GoNatureServer getInstance(int port, ServerUIFrameController controller) throws Exception {
        if (server == null) {
            server = new GoNatureServer(port, controller);
        }
        return server;
    }

    //Instance methods ************************************************

    /**
     * This method handles any messages received from the client.
     *
     * @param msg The message received from the client.
     * @param client The connection from which the message originated.
     * @param
     */

    /**
     * This method overrides the one in the superclass.  Called
     * when the server starts listening for connections.
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
            Workers.changeToAbsentOrders(db,controller);
            server.controller.toggleControllers(true);
        } catch (Exception e) {
            controller.addtolog("Error occured on Server : " + e.getMessage());
        }
    }


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
                case OP_GET_DEPARTMENT_MANAGER_PARKS:
                    handleGetDepartmentParkNames(message, client);
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

    private void handleSyncHandshake(Message message, ConnectionToClient client) throws IOException {
        client.sendToClient(message);
    }

    private void handleLogout(Message message, ConnectionToClient client) throws IOException {
        signedInInstances.remove(message.getMsgUserName());
        client.sendToClient("logged out successfully");
    }

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

    private void handleActivateGroupGuide(Message message, ConnectionToClient client) throws Exception {
        String groupGuideID = (String) message.getMsgData();
        String retVal = db.activateGroupGuide(groupGuideID);
        Message registerGroupGuideMessage = new Message(OpCodes.OP_ACTIVATE_GROUP_GUIDE, null, retVal);
        client.sendToClient(registerGroupGuideMessage);
    }

    private void handleGetRequestsFromParkManager(Message message, ConnectionToClient client) throws Exception {
        Integer departmentID = (Integer) message.getMsgData();
        ArrayList<RequestChangingParkParameters> requests = db.getRequestsFromParkManager(departmentID);
        Message retrieveRequestsMsg = new Message(OpCodes.OP_GET_REQUESTS_FROM_PARK_MANAGER, message.getMsgUserName(), requests);
        client.sendToClient(retrieveRequestsMsg);
    }

    private void handleAuthorizeParkRequest(Message message, ConnectionToClient client) throws Exception {
        RequestChangingParkParameters authRequest = (RequestChangingParkParameters) message.getMsgData();
        boolean isAuthorized = db.authorizeParkRequest(authRequest);
        Message authorizeRequestMsg = new Message(OpCodes.OP_AUTHORIZE_PARK_REQUEST, message.getMsgUserName(), isAuthorized);
        client.sendToClient(authorizeRequestMsg);
    }

    private void handleDeclineParkRequest(Message message, ConnectionToClient client) throws Exception {
        RequestChangingParkParameters unauthRequest = (RequestChangingParkParameters) message.getMsgData();
        boolean isUnauthorized = db.unauthorizeParkRequest(unauthRequest);
        Message unauthorizeRequestMsg = new Message(OpCodes.OP_DECLINE_PARK_REQUEST, message.getMsgUserName(), isUnauthorized);
        client.sendToClient(unauthorizeRequestMsg);
    }

    private void handleSubmitRequestsToDepartment(Message message, ConnectionToClient client) throws Exception {
        Map<ParkParameters, RequestChangingParkParameters> requestMap = (Map<ParkParameters, RequestChangingParkParameters>) message.getMsgData();
        boolean isSubmitted = db.submitRequestsToDepartment(requestMap);
        Message submitRequestMsg = new Message(OpCodes.OP_SUBMIT_REQUESTS_TO_DEPARTMENT, message.getMsgUserName(), isSubmitted);
        client.sendToClient(submitRequestMsg);
    }

    private void handleGetParkDetailsByParkID(Message message, ConnectionToClient client) throws Exception {
        String ParkID = (String) message.getMsgData();
        Park park = db.getParkDetails(ParkID);
        Message submitRequestMsg = new Message(OpCodes.OP_GET_PARK_DETAILS_BY_PARK_ID, message.getMsgUserName(), park);
        client.sendToClient(submitRequestMsg);
    }

    private void handleQuit(ConnectionToClient client) throws Exception {
        signedInInstances.values().removeIf(value -> value == client);
        controller.addtolog("Client " + client + " Disconnected");
        controller.removeRowByIP(client.getInetAddress().getHostAddress());
        client.close();
    }

    private void handleCancelOrderVisitation(Message message, ConnectionToClient client) throws Exception {
        Order order = (Order) message.getMsgData();
        String orderID = order.getOrderID();
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

    private void handleGetOrderByID(Message message, ConnectionToClient client) throws Exception {
        String orderID = (String) message.getMsgData();
        Order order = db.getOrderById(orderID);
        Message respondMsg = new Message(OpCodes.OP_GET_ORDER_BY_ID, message.getMsgUserName(), order);
        client.sendToClient(respondMsg);
    }

    private void handleUpdateExitTimeOfOrder(Message message, ConnectionToClient client) throws Exception {
        String orderID = message.getMsgData().toString();
        String answer = db.setExitTimeOfOrder(orderID);
        Message respondMsg = new Message(OpCodes.OP_UPDATE_EXIT_TIME_OF_ORDER, null, answer);
        client.sendToClient(respondMsg);
    }

    private void handleUpdateOrderDetailsByOrderId(Message message, ConnectionToClient client) throws Exception {
        String[] details = (String[]) message.getMsgData();
        boolean isUpdated = db.updateOrderDetails(details);
        Message respondMsg = new Message(OpCodes.OP_UPDATE_ORDER_DETAILS_BY_ORDERID, message.getMsgUserName(), isUpdated);
        client.sendToClient(respondMsg);
    }

    private void handleMarkOrderAsPaid(Message message, ConnectionToClient client) throws Exception {
        Order order = (Order) message.getMsgData();
        boolean isMarkedAsPaid = db.markOrderAsPaid(order);
        Message respondMsg = new Message(OpCodes.OP_MARK_ORDER_AS_PAID, message.getMsgUserName(), isMarkedAsPaid);
        client.sendToClient(respondMsg);
    }

    private void handleViewReportBlob(Message message, ConnectionToClient client) throws Exception {
        String[] params = (String[]) message.getMsgData();
        byte[] pdfBlob = db.getReportBlob(Boolean.parseBoolean(params[0]), params[1], params[2], params[3], params[4]);
        Message respondMsg = new Message(OpCodes.OP_VIEW_REPORT_BLOB, message.getMsgUserName(), pdfBlob);
        client.sendToClient(respondMsg);
    }

    private void handleGenerateReportBlob(Message message, ConnectionToClient client) throws Exception {
        String reportType = (String) message.getMsgData();
        boolean isGenerated = false;
        String id = null; // used for department ID (for first 2 cases) or park ID (for other cases)
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

    public static Timestamp createExitTime(Timestamp enterTime, int expectedTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(enterTime.getTime());
        cal.add(Calendar.MINUTE, expectedTime);
        return (new Timestamp(cal.getTimeInMillis()));
    }


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

    private void handleGetDepartmentParkNames(Message message, ConnectionToClient client) throws Exception {
        Integer departmentID = (Integer) message.getMsgData();
        ArrayList<String> parkNames = db.getDepartmentParkNames(departmentID);
        Message respondMsg = new Message(OpCodes.OP_GET_DEPARTMENT_MANAGER_PARKS, message.getMsgUserName(), parkNames);
        client.sendToClient(respondMsg);
    }

    /**
     * This method overrides the one in the superclass.  Called
     * when the server stops listening for connections.
     */
    @Override
    protected void serverStopped() {
        this.controller.addtolog("Server has stopped listening for connections.");
        if (db != null) {
            db.closeConnection();
        }
        Workers.shutdownExecutors();
    }

    @Override
    protected void serverClosed() {
        controller.toggleControllers(false);
        this.controller.addtolog("Server has closed.");
        if (db != null) {
            db.closeConnection();
        }
        Workers.shutdownExecutors();
    }

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
