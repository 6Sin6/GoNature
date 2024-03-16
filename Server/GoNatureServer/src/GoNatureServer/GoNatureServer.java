// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 
package GoNatureServer;

import CommonServer.ocsf.AbstractServer;
import CommonServer.ocsf.ConnectionToClient;
import DataBase.DBConnection;
import Entities.*;
import ServerUIPageController.ServerPortFrameController;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
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
    private ServerPortFrameController controller;
    private DBConnection db;

    private Map<String, ConnectionToClient> authenticatedUsers = new ConcurrentHashMap<>();

    private boolean connected = true;


    //Constructors ****************************************************

    /**
     * Constructs an instance of the echo server.
     *
     * @param port The port number to connect on.
     */

    private GoNatureServer(int port, ServerPortFrameController controller) throws Exception {
        super(port);
        this.controller = controller;
    }

    public void initializeDBConnection(ServerPortFrameController controller) throws Exception {
        try {
            db = DBConnection.getInstance(controller);
        } catch (ClassNotFoundException | SQLException e) {
            controller.addtolog(e.getMessage());
            db = null;
            controller.addtolog("Server failed to initialize");
            connected = false;
        }
    }

    public static GoNatureServer getInstance(int port, ServerPortFrameController controller) throws Exception {
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
        this.controller.addtolog("Server listening for connections on port " + getPort());
        Workers.startClientProcessingThread(controller, this);
        Workers.SendReminderDayBeforeWorker(db, controller);
        Workers.CancelOrdersThatDidntConfirmWorker(db, controller);
        server.controller.toggleControllers(true);
    }


    public void handleMessageFromClient(Object msg, ConnectionToClient client) throws IOException {
        if (!(msg instanceof Message)) {
            return;
        }
        Message message = (Message) msg;
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
            case OP_REGISTER_GROUP_GUIDE:
                handleRegisterGroupGuide(message, client);
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
            case OP_QUIT:
                handleQuit(client);
                break;
            default:
                controller.addtolog("Error Unknown Opcode");
        }
    }

    private void handleSyncHandshake(Message message, ConnectionToClient client) throws IOException {
        client.sendToClient(message);
    }

    private void handleLogout(Message message, ConnectionToClient client) throws IOException {
        authenticatedUsers.remove(message.getMsgUserName());
        client.sendToClient("logged out successfully");
    }

    private void handleSignIn(Message message, ConnectionToClient client) throws IOException {
        User userCredentials = (User) message.getMsgData();
        if (authenticatedUsers.containsKey(userCredentials.getUsername())) {
            if (authenticatedUsers.get(userCredentials.getUsername()).getInetAddress() == null) {
                authenticatedUsers.remove(userCredentials.getUsername());
            } else {
                Message respondMsg = new Message(OpCodes.OP_SIGN_IN_ALREADY_LOGGED_IN, userCredentials.getUsername(), null);
                client.sendToClient(respondMsg);
                return;
            }
        }
        User authenticatedUser = db.login(userCredentials.getUsername(), userCredentials.getPassword());
        if (authenticatedUser == null) {
            Message respondMsg = new Message(OpCodes.OP_SIGN_IN, "", null);
            client.sendToClient(respondMsg);
            return;
        }
        authenticatedUsers.put(authenticatedUser.getUsername(), client);
        Message respondMsg = new Message(OpCodes.OP_SIGN_IN, authenticatedUser.getUsername(), authenticatedUser);
        client.sendToClient(respondMsg);
    }

    private void handleGetVisitorOrders(Message message, ConnectionToClient client) throws IOException {
        AbstractVisitor visitor = (AbstractVisitor) message.getMsgData();
        ArrayList<Order> requestedOrders = db.getUserOrders(visitor.getID());
        OrderBank orders = visitor instanceof SingleVisitor ?
                new OrderBank(OrderType.ORD_TYPE_SINGLE) :
                new OrderBank(OrderType.ORD_TYPE_GROUP);

        if (orders.insertOrderArray(requestedOrders)) {
            Message getVisitorOrdersMsg = new Message(OpCodes.OP_GET_VISITOR_ORDERS, visitor.getUsername(), requestedOrders);
            client.sendToClient(getVisitorOrdersMsg);
        } else {
            Message getVisitorOrdersMsg = new Message(OpCodes.OP_DB_ERR);
            client.sendToClient(getVisitorOrdersMsg);
        }
    }

    private void handleCreateNewVisitation(Message message, ConnectionToClient client) throws IOException {
        Order order = (Order) message.getMsgData();
        order.setOrderStatus(OrderStatus.STATUS_CONFIRMED_PENDING_PAYMENT);

        if (db.checkOrderExists(order.getVisitorID(), order.getParkID(), order.getVisitationDate())) {
            Message createOrderMsg = new Message(OpCodes.OP_ORDER_ALREADY_EXIST);
            client.sendToClient(createOrderMsg);
            return;
        }
        Order newOrder = db.addOrder(order);
        Message createOrderMsg = new Message(OpCodes.OP_CREATE_NEW_VISITATION, message.getMsgUserName(), newOrder);
        client.sendToClient(createOrderMsg);
    }

    private void handleGetUserOrdersByUserID(Message message, ConnectionToClient client) throws IOException {
        String[] data = (String[]) message.getMsgData();
        Order userOrder = db.getUserOrderByUserID(data[0], data[1]);
        Message getUserMsg = new Message(OpCodes.OP_GET_USER_ORDERS_BY_USERID_ORDERID, "", userOrder);
        client.sendToClient(getUserMsg);
    }

    private void handleRegisterGroupGuide(Message message, ConnectionToClient client) throws IOException {
        String newGroupGuideID = (String) message.getMsgData();
        int retValue = db.registerGroupGuide(newGroupGuideID);
        Message registerGroupGuideMessage;
        switch (retValue) {
            case 0:
                registerGroupGuideMessage = new Message(OpCodes.OP_VISITOR_ID_DOESNT_EXIST);
                break;
            case 1:
                registerGroupGuideMessage = new Message(OpCodes.OP_VISITOR_IS_ALREADY_GROUP_GUIDE);
                break;
            case 2:
                registerGroupGuideMessage = new Message(OpCodes.OP_UPDATED_VISITOR_TO_GROUP_GUIDE);
                break;
            default:
                registerGroupGuideMessage = new Message(OpCodes.OP_DB_ERR);
                break;
        }
        client.sendToClient(registerGroupGuideMessage);
    }

    private void handleGetRequestsFromParkManager(Message message, ConnectionToClient client) throws IOException {
        Integer departmentID = (Integer) message.getMsgData();
        ArrayList<RequestChangingParkParameters> requests = db.getRequestsFromParkManager(departmentID);
        Message retrieveRequestsMsg = new Message(OpCodes.OP_GET_REQUESTS_FROM_PARK_MANAGER, message.getMsgUserName(), requests);
        client.sendToClient(retrieveRequestsMsg);
    }

    private void handleAuthorizeParkRequest(Message message, ConnectionToClient client) throws IOException {
        RequestChangingParkParameters authRequest = (RequestChangingParkParameters) message.getMsgData();
        boolean isAuthorized = db.authorizeParkRequest(authRequest);
        Message authorizeRequestMsg = new Message(OpCodes.OP_AUTHORIZE_PARK_REQUEST, message.getMsgUserName(), isAuthorized);
        client.sendToClient(authorizeRequestMsg);
    }

    private void handleDeclineParkRequest(Message message, ConnectionToClient client) throws IOException {
        RequestChangingParkParameters unauthRequest = (RequestChangingParkParameters) message.getMsgData();
        boolean isUnauthorized = db.unauthorizeParkRequest(unauthRequest);
        Message unauthorizeRequestMsg = new Message(OpCodes.OP_DECLINE_PARK_REQUEST, message.getMsgUserName(), isUnauthorized);
        client.sendToClient(unauthorizeRequestMsg);
    }

    private void handleSubmitRequestsToDepartment(Message message, ConnectionToClient client) throws IOException {
        Map<ParkParameters, RequestChangingParkParameters> requestMap = (Map<ParkParameters, RequestChangingParkParameters>) message.getMsgData();
        boolean isSubmitted = db.submitRequestsToDepartment(requestMap);
        Message submitRequestMsg = new Message(OpCodes.OP_SUBMIT_REQUESTS_TO_DEPARTMENT, message.getMsgUserName(), isSubmitted);
        client.sendToClient(submitRequestMsg);
    }

    private void handleGetParkDetailsByParkID(Message message, ConnectionToClient client) throws IOException {
        String ParkID = (String) message.getMsgData();
        Park park = db.getParkDetails(ParkID);
        Message submitRequestMsg = new Message(OpCodes.OP_GET_PARK_DETAILS_BY_PARK_ID, message.getMsgUserName(), park);
        client.sendToClient(submitRequestMsg);
    }

    private void handleQuit(ConnectionToClient client) throws IOException {
        authenticatedUsers.values().removeIf(value -> value == client);
        controller.addtolog("Client " + client + " Disconnected");
        controller.removeRowByIP(client.getInetAddress().getHostAddress());
        client.close();
    }

    private void handleCancelOrderVisitation(Message message, ConnectionToClient client) throws IOException {
        Order order = (Order) message.getMsgData();
        String orderID = order.getOrderID();
        boolean isCanceled = db.updateOrderStatusAsCancelled(orderID);
        if (!isCanceled) {
            Message respondMsg = new Message(OpCodes.OP_DB_ERR, null, null);
            client.sendToClient(respondMsg);
        }
        Message respondMsg = new Message(OpCodes.OP_HANDLE_VISITATION_CANCEL_ORDER, message.getMsgUserName(), isCanceled);
        client.sendToClient(respondMsg);
    }

    private void handleGetOrderByID(Message message, ConnectionToClient client) throws IOException {
        String orderID = (String) message.getMsgData();
        Order order = db.getOrderById(orderID);
        Message respondMsg = new Message(OpCodes.OP_GET_ORDER_BY_ID, message.getMsgUserName(), order);
        client.sendToClient(respondMsg);
    }

    private void handleUpdateOrderDetailsByOrderId(Message message, ConnectionToClient client) throws IOException {
        Order order = (Order) message.getMsgData();
        String[] details = new String[3];
        details[0] = order.getOrderID();
        details[1] = order.getPhoneNumber();
        details[2] = order.getClientEmailAddress();
        boolean isCanceled = db.updateOrderDetails(details);
        Message respondMsg = new Message(OpCodes.OP_UPDATE_ORDER_DETAILS_BY_ORDERID, message.getMsgUserName(), isCanceled);
        client.sendToClient(respondMsg);
    }

    private void handleMarkOrderAsPaid(Message message, ConnectionToClient client) throws IOException {
        Order order = (Order) message.getMsgData();
        boolean isMarkedAsPaid = db.markOrderAsPaid(order);
        Message respondMsg = new Message(OpCodes.OP_MARK_ORDER_AS_PAID, message.getMsgUserName(), isMarkedAsPaid);
        client.sendToClient(respondMsg);
    }


    /**
     * This method overrides the one in the superclass.  Called
     * when the server stops listening for connections.
     */
    @Override
    protected void serverStopped() {
        this.controller.addtolog("Server has stopped listening for connections.");
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
