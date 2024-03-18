package DataBase;

import Entities.*;
import ServerUIPageController.ServerPortFrameController;

import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

import static CommonUtils.CommonUtils.convertMinutesToTimestamp;

/**
 * Manages the database connection for the application.
 * This class is responsible for initializing the JDBC driver,
 * establishing a connection to the database, and providing access
 * to the established connection via a singleton pattern.
 */
public class DBConnection {
    private Connection conn;
    private static DBConnection dbConnection;
    private String schemaName;

    private final ServerPortFrameController serverController;

    private DBController dbController;

    /**
     * Private constructor to prevent instantiation.
     * Initializes the JDBC driver and sets up the database connection
     * using the provided server port frame controller.
     *
     * @param controller The server port frame controller used for retrieving
     *                   connection details and logging.
     */
    private DBConnection(ServerPortFrameController controller) throws ClassNotFoundException, SQLException {
        this.serverController = controller;
        if (!driverDefinition()) {
            throw new ClassNotFoundException("Driver definition failed");
        }
        if (!setConnection(controller.getURLComboBox(), controller.getUserName(), controller.getPassword())) {
            throw new SQLException("SQL connection failed");
        }
        this.dbController = new DBController(conn);
    }

    /**
     * Provides the singleton instance of the DBConnection.
     * If the instance does not exist, it is created using the provided controller.
     *
     * @param controller The server port frame controller used for retrieving
     *                   connection details and logging.
     * @return The singleton instance of DBConnection.
     */
    public static DBConnection getInstance(ServerPortFrameController controller) throws Exception {
        if (dbConnection == null) {
            dbConnection = new DBConnection(controller);
        }
        return dbConnection;
    }

    /**
     * Initializes the JDBC driver.
     * Logs the outcome of the driver initialization process.
     */
    private boolean driverDefinition() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            this.serverController.addtolog("Driver definition succeed");
            return true;
        } catch (Exception ex) {
            this.serverController.addtolog("Driver definition failed");
            return false;
        }
    }

    /**
     * Sets up the database connection using the provided URL, username, and password.
     * Logs the outcome of the connection process.
     *
     * @param url      The URL of the database.
     * @param user     The username for the database.
     * @param password The password for the database.
     */
    private boolean setConnection(String url, String user, String password) {
        try {
            this.schemaName = "gonature";
            this.conn = DriverManager.getConnection("jdbc:mysql://" + url + ":3306/" + this.schemaName + "?serverTimezone=Asia/Jerusalem&useSSL=false&allowPublicKeyRetrieval=true", user, password);
            this.serverController.addtolog("SQL connection succeed");
            return true;
        } catch (SQLException ex) {
            logSQLException(ex);
            return false;
        }
    }

    // Utility method to log SQL exceptions
    private void logSQLException(SQLException ex) {
        this.serverController.addtolog("SQLException: " + ex.getMessage());
        this.serverController.addtolog("SQLState: " + ex.getSQLState());
        this.serverController.addtolog("VendorError: " + ex.getErrorCode());
    }

    public void closeConnection() {
        try {
            this.conn.close();
        } catch (Exception ignored) {
        }
        this.serverController.addtolog("SQL connection closed");
        this.conn = null;
        dbConnection = null;
    }


    //=================================================================================================================//
    //                                                                                                                 //
    //                                           VISITOR & USERS METHODS                                               //
    //                                                                                                                 //
    //=================================================================================================================//
    public User login(String username, String password) {
        try {
            String tableName = this.schemaName + ".users";
            String whereClause = "username='" + username + "' AND password='" + password + "'";
            ResultSet userCredentials = dbController.selectRecords(tableName, whereClause);

            if (userCredentials.next()) {
                int userRole = userCredentials.getInt("role");
                String userTypeTableName = userRole < 3 ? ".visitors" : userRole == 4 ? ".departmentmanagers" : ".parkemployees";
                ResultSet userGoNatureData =
                        dbController.selectRecords(this.schemaName + userTypeTableName, "username='" + username + "'");

                if (userGoNatureData.next()) {
                    switch (userRole) {
                        case 1:
                            return new SingleVisitor(
                                    userGoNatureData.getString("VisitorID")
                            );
                        case 2:
                            return new VisitorGroupGuide(
                                    userCredentials.getString("username"),
                                    "",
                                    userGoNatureData.getString("emailAddress"),
                                    userGoNatureData.getString("VisitorID"),
                                    userGoNatureData.getString("firstName"),
                                    userGoNatureData.getString("lastName")
                            );
                        case 2:
                            ResultSet parkData = dbController.selectRecords(this.schemaName + ".parks", "ParkID=" + userGoNatureData.getString("ParkID"));
                            ResultSet managerData = dbController.selectRecords(this.schemaName + ".parkemployees", "ParkID=" + userGoNatureData.getString("ParkID") + " AND isParkManager=true");
                            if (parkData.next() && managerData.next()) {
                                return new ParkEmployee(
                                        userCredentials.getString("firstname"),
                                        userCredentials.getString("lastname"),
                                        userCredentials.getString("username"),
                                        "",
                                        userGoNatureData.getString("EmailAddress"),
                                        new Park(
                                                parkData.getString("ParkID"),
                                                parkData.getString("ParkName"),
                                                parkData.getInt("Capacity"),
                                                parkData.getInt("GapVisitorsCapacity"),
                                                convertMinutesToTimestamp(parkData.getInt("DefaultVisitationTime")),
                                                parkData.getInt("departmentID"),
                                                new ParkManager(
                                                        managerData.getString("username"),
                                                        "",
                                                        managerData.getString("EmailAddress"),
                                                        parkData.getString("ParkID")
                                                )
                                        )
                                );
                            }
                            break;
                        case 3:
                            return new ParkDepartmentManager(
                                    userCredentials.getString("username"),
                                    "",
                                    userGoNatureData.getString("emailAddress"),
                                    null,
                                    null,
                                    userGoNatureData.getInt("departmentID")
                            );
                        case 4:
                            return new ParkManager(
                                    userCredentials.getString("username"),
                                    "",
                                    userGoNatureData.getString("EmailAddress"),
                                    userGoNatureData.getString("ParkID")
                            );
                        case 5:
                            ResultSet parkDataSupport = dbController.selectRecords(this.schemaName + ".parks", "ParkID=" + userGoNatureData.getString("ParkID"));
                            ResultSet supportManagerData = dbController.selectRecords(this.schemaName + ".parkemployees", "ParkID=" + userGoNatureData.getString("ParkID") + " AND isParkManager=true");
                            if (parkDataSupport.next() && supportManagerData.next()) {
                                return new ParkSupportRepresentative(
                                        userCredentials.getString("username"),
                                        "",
                                        userGoNatureData.getString("emailAddress"),
                                        new Park(
                                                parkDataSupport.getString("ParkID"),
                                                parkDataSupport.getString("ParkName"),
                                                parkDataSupport.getInt("Capacity"),
                                                parkDataSupport.getInt("GapVisitorsCapacity"),
                                                convertMinutesToTimestamp(parkDataSupport.getInt("DefaultVisitationTime")),
                                                parkDataSupport.getInt("departmentID"),
                                                new ParkManager(
                                                        supportManagerData.getString("username"),
                                                        "",
                                                        supportManagerData.getString("EmailAddress"),
                                                        parkDataSupport.getString("ParkID")
                                                )
                                        )
                                );
                            }
                        default:
                            break;
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            this.serverController.addtolog(e.getMessage());
            return null;
        }
    }

    public Order getUserOrderByUserID(String userID, String orderID) {
        try {
            String tableName = this.schemaName + ".visitors";
            String whereClause = "VisitorID='" + userID + "'";
            ResultSet userGoNatureData = dbController.selectRecords(tableName, whereClause);
            if (userGoNatureData.next()) {
                ResultSet orderData = dbController.selectRecords(this.schemaName + ".orders", "VisitorID='" + userID + "' AND OrderID=' " + orderID + "'");
                if (orderData.next()) {
                    return new Order(
                            orderData.getString("VisitorID"),
                            orderData.getString("ParkID"),
                            orderData.getTimestamp("VisitationDate"),
                            orderData.getString("ClientEmailAddress"),
                            orderData.getString("PhoneNumber"),
                            OrderStatus.values()[orderData.getInt("orderStatus") - 1],
                            orderData.getTimestamp("EnteredTime"),
                            orderData.getTimestamp("ExitedTime"),
                            orderData.getString("OrderID"),
                            OrderType.values()[orderData.getInt("OrderType") - 1],
                            orderData.getInt("NumOfVisitors")
                    );
                }
            }
            return null;
        } catch (SQLException e) {
            this.serverController.addtolog(e.getMessage());
            return null;
        }
    }

    public Order addOrder(Order order) {
        try {
            String tableName = this.schemaName + ".orders";
            String columns = "VisitorID, ParkID, VisitationDate, ClientEmailAddress, PhoneNumber, orderStatus, EnteredTime, ExitedTime, OrderType, NumOfVisitors";
            if (!dbController.insertRecord(tableName, columns, "'" + order.getVisitorID() + "'",
                    "'" + order.getParkID() + "'",
                    "'" + order.getVisitationDate().toString().substring(0, order.getVisitationDate().toString().length() - 2) + "'",
                    "'" + order.getClientEmailAddress() + "'",
                    "'" + order.getPhoneNumber() + "'",
                    String.valueOf(order.getOrderStatus().ordinal() + 1),
                    "'" + order.getEnteredTime().toString().substring(0, order.getVisitationDate().toString().length() - 2) + "'",
                    "'" + order.getExitedTime().toString().substring(0, order.getVisitationDate().toString().length() - 2) + "'",
                    String.valueOf(order.getOrderType().ordinal() + 1),
                    String.valueOf(order.getNumOfVisitors()))) {
                this.serverController.addtolog("Insert into " + tableName + " failed. Insert order:" + order);
                return null;
            }
            this.serverController.addtolog("Insert into " + tableName + " succeeded. Insert order:" + order);

            // Get the order from the DB (extract newly assigned order ID)
            ResultSet results = dbController.selectRecords(tableName, "VisitorID='" + order.getVisitorID() + "' AND ParkID='" + order.getParkID() + "' AND VisitationDate='" + order.getVisitationDate() + "'");
            if (results.next()) {
                return new Order(
                        results.getString("VisitorID"),
                        results.getString("ParkID"),
                        results.getTimestamp("VisitationDate"),
                        results.getString("ClientEmailAddress"),
                        results.getString("PhoneNumber"),
                        OrderStatus.values()[results.getInt("orderStatus") - 1],
                        results.getTimestamp("EnteredTime"),
                        results.getTimestamp("ExitedTime"),
                        results.getString("OrderID"),
                        OrderType.values()[results.getInt("OrderType") - 1],
                        results.getInt("NumOfVisitors")
                );
            }
            return null;
        } catch (SQLException e) {
            this.serverController.addtolog(e.getMessage());
            return null;
        }
    }

    public Order getOrderById(String OrderID) {
        try {
            String tableName = this.schemaName + ".orders";
            String whereClause = "OrderID=" + OrderID;
            ResultSet results = dbController.selectRecords(tableName, whereClause);
            if (results.next()) {
                return new Order(
                        results.getString("VisitorID"),
                        results.getString("ParkID"),
                        results.getTimestamp("VisitationDate"),
                        results.getString("ClientEmailAddress"),
                        results.getString("PhoneNumber"),
                        OrderStatus.values()[results.getInt("orderStatus") - 1],
                        results.getTimestamp("EnteredTime"),
                        results.getTimestamp("ExitedTime"),
                        results.getString("OrderID"),
                        OrderType.values()[results.getInt("OrderType") - 1],
                        results.getInt("NumOfVisitors")
                );
            }
            return new Order("", "", null, "", "", null, null, null, "", null, 0);
        } catch (SQLException e) {
            this.serverController.addtolog(e.getMessage());
            return null;
        }
    }

    public ArrayList<Order> getUserOrders(String visitorID) {
        try {
            String tableName = this.schemaName + ".orders";
            String whereClause = "VisitorID=" + visitorID;
            ResultSet results = dbController.selectRecords(tableName, whereClause);

            ArrayList<Order> orders = new ArrayList<>();
            while (results.next()) {
                orders.add(new Order(
                        results.getString("VisitorID"),
                        results.getString("ParkID"),
                        results.getTimestamp("VisitationDate"),
                        results.getString("ClientEmailAddress"),
                        results.getString("PhoneNumber"),
                        OrderStatus.values()[results.getInt("orderStatus") - 1],
                        results.getTimestamp("EnteredTime"),
                        results.getTimestamp("ExitedTime"),
                        results.getString("OrderID"),
                        OrderType.values()[results.getInt("OrderType") - 1],
                        results.getInt("NumOfVisitors")
                ));
            }
            return orders;
        } catch (SQLException e) {
            this.serverController.addtolog(e.getMessage());
            return null;
        }
    }

    public boolean updateOrderStatus(String orderID, OrderStatus status) {
        try {
            String tableName = this.schemaName + ".orders";
            String setClause = "orderStatus=" + status.getOrderStatus();
            String whereClause = "OrderID=" + orderID;
            if (!dbController.updateRecord(tableName, setClause, whereClause)) {
                this.serverController.addtolog("Update in " + tableName + " failed. Update order status:" + orderID);
                return false;
            }
            return true;
        } catch (SQLException e) {
            this.serverController.addtolog(e.getMessage());
            return false;
        }
    }

    public boolean updateOrderDetails(String[] details) {
        try {
            String tableName = this.schemaName + ".orders";
            String setClause = "ClientEmailAddress='" + details[2] + "' , PhoneNumber='" + details[1] + "'";
            String whereClause = "OrderID='" + details[0] + "'";
            if (!this.dbController.updateRecord(tableName, setClause, whereClause)) {
                this.serverController.addtolog("Update in " + tableName + " failed. Update order status:" + details[0]);
                return false;
            } else {
                return true;
            }
        } catch (SQLException e) {
            this.serverController.addtolog(e.getMessage());
            return false;
        }
    }

    public boolean updateOrderStatusAsCancelled(String orderID) {
        return updateOrderStatus(orderID, OrderStatus.STATUS_CANCELLED);
    }

    public boolean checkOrderExists(String visitorID, String parkID, Timestamp visitationDate) {
        try {
            String tableName = this.schemaName + ".orders";
            String whereClause = "VisitorID=" + visitorID + " AND ParkID=" + parkID + " AND VisitationDate= '" + visitationDate + "'";
            ResultSet results = dbController.selectRecords(tableName, whereClause);
            return results.next();
        } catch (SQLException e) {
            this.serverController.addtolog(e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves the details of a specific park from the database.
     * The method takes a park ID as a parameter and fetches the corresponding park details.
     * It also fetches the details of the park manager associated with the park.
     *
     * @param parkID The ID of the park for which to fetch the details.
     * @return A Park object containing the details of the park and its manager.
     * Returns a new Park object with default values if no matching park is found.
     * Returns null if a SQLException is thrown.
     * @throws SQLException If there is an error while fetching the park details.
     */
    public Park getParkDetails(String parkID) {
        try {
            String tableName = this.schemaName + ".parks";
            String whereClause = "ParkID=" + parkID;
            ResultSet results = dbController.selectRecords(tableName, whereClause);
            if (results.next()) {
                String pManagerId = results.getString("ParkManagerID");
                ResultSet managerResults = dbController.selectRecordsFields(this.schemaName + ".parkemployees", "id=" + pManagerId, "username", "EmailAddress");
                if (managerResults.next()) {
                    return new Park(
                            results.getString("ParkID"),
                            results.getString("ParkName"),
                            results.getInt("Capacity"),
                            results.getInt("GapVisitorsCapacity"),
                            convertMinutesToTimestamp(results.getInt("DefaultVisitationTime")),
                            results.getInt("departmentID"),
                            new ParkManager(
                                    managerResults.getString("username"),
                                    "",
                                    managerResults.getString("EmailAddress"),
                                    results.getString("ParkID")
                            )
                    );
                }
            }
            return new Park("", "", 0, 0, null, 0, null);
        } catch (SQLException e) {
            this.serverController.addtolog(e.getMessage());
            return null;
        }
    }

    //=================================================================================================================//
    //                                                                                                                 //
    //                                           PARK EMPLOYEES EXCLUSIVE METHODS                                      //
    //                                                                                                                 //
    //=================================================================================================================//

    public boolean markOrderAsPaid(Order order) {
        try {
            String orderID = order.getOrderID();
            String tableName = this.schemaName + ".orders";
            String setClause = "orderStatus=" + OrderStatus.STATUS_CONFIRMED_PAID.getOrderStatus();
            String whereClause = "OrderID=" + orderID;
            if (!dbController.updateRecord(tableName, setClause, whereClause)) {
                this.serverController.addtolog("Update in " + tableName + " failed. Mark order as paid:" + orderID);
                return false;
            }

            String tableName2 = this.schemaName + ".payments";
            String columns = "OrderID, hasPaid, price";

            if (!dbController.insertRecord(tableName2, columns, orderID, "true", String.valueOf(order.getNumOfVisitors() * Order.pricePerVisitor))) {
                this.serverController.addtolog("Insert into " + this.schemaName + ".payments failed. Mark order as paid:" + orderID);
                return false;
            }
            return true;
        } catch (SQLException e) {
            this.serverController.addtolog(e.getMessage());
            return false;
        }
    }

    //=================================================================================================================//
    //                                                                                                                 //
    //                                           MANAGERS EXCLUSIVE METHODS                                            //
    //                                                                                                                 //
    //=================================================================================================================//
    public ArrayList<RequestChangingParkParameters> getRequestsFromParkManager(Integer departmentID) {
        try {
            String tableName = this.schemaName + ".requeststodepmanager";
            String whereClause = "DepartmentID=" + departmentID + " AND status=" + RequestStatus.REQUEST_PENDING.getRequestStatus();
            ResultSet results = dbController.selectRecords(tableName, whereClause);
            ArrayList<RequestChangingParkParameters> requests = new ArrayList<>();
            while (results.next()) {
                ResultSet parkResults = dbController.selectRecords(this.schemaName + ".parks", "ParkID=" + results.getString("ParkID"));
                if (parkResults.next()) {
                    ResultSet parkManagerResults = dbController.selectRecords(this.schemaName + ".parkemployees", "id=" + parkResults.getString("ParkManagerID") + " AND isParkManager=true");
                    if (parkManagerResults.next()) {
                        requests.add(new RequestChangingParkParameters(
                                new Park(
                                        parkResults.getString("ParkID"),
                                        parkResults.getString("ParkName"),
                                        parkResults.getInt("Capacity"),
                                        parkResults.getInt("GapVisitorsCapacity"),
                                        convertMinutesToTimestamp(parkResults.getInt("DefaultVisitationTime")),
                                        parkResults.getInt("DepartmentID"),
                                        new ParkManager(
                                                parkManagerResults.getString("username"),
                                                "",
                                                parkManagerResults.getString("EmailAddress"),
                                                parkManagerResults.getString("ParkID")
                                        )
                                ),
                                ParkParameters.values()[results.getInt("Parameter") - 1],
                                results.getDouble("RequestedValue"),
                                RequestStatus.values()[results.getInt("Status") - 1]
                        ));
                    }
                }
            }
            return requests;
        } catch (SQLException e) {
            this.serverController.addtolog(e.getMessage());
            return null;
        }
    }

    public boolean authorizeParkRequest(RequestChangingParkParameters req) {
        try {
            String tableName = this.schemaName + ".requeststodepmanager";
            String setClause = "handleDate = CURRENT_TIMESTAMP(), status=" + RequestStatus.REQUEST_ACCEPTED.getRequestStatus();
            String whereClause = "ParkID=" + req.getPark().getParkID() + " AND parameter=" + req.getParameter().getParameterVal() + " AND requestedValue=" + req.getRequestedValue();
            if (!dbController.updateRecord(tableName, setClause, whereClause)) {
                this.serverController.addtolog("Update in " + tableName + " failed. Authorize park request:" + req);
                return false;
            }

            String tableName2 = this.schemaName + ".parks";
            String setClause2 = req.getParameter().getColumnName() + "=" + req.getRequestedValue();
            String whereClause2 = "ParkID=" + req.getPark().getParkID();
            if (!dbController.updateRecord(tableName2, setClause2, whereClause2)) {
                this.serverController.addtolog("Update in " + tableName2 + " failed. Authorize park request:" + req);
                return false;
            }
            return true;
        } catch (SQLException e) {
            this.serverController.addtolog(e.getMessage());
            return false;
        }
    }

    public boolean unauthorizeParkRequest(RequestChangingParkParameters req) {
        try {
            String tableName = this.schemaName + ".requeststodepmanager";
            String setClause = "Status=" + RequestStatus.REQUEST_DECLINED.getRequestStatus();
            String whereClause = "ParkID=" + req.getPark().getParkID() + " AND parameter=" + req.getParameter().getParameterVal() + " AND requestedValue=" + req.getRequestedValue();
            if (!dbController.updateRecord(tableName, setClause, whereClause)) {
                this.serverController.addtolog("Update in " + tableName + " failed. Authorize park request:" + req);
                return false;
            }
            return true;
        } catch (SQLException e) {
            this.serverController.addtolog(e.getMessage());
            return false;
        }
    }

    public boolean submitRequestsToDepartment(Map<ParkParameters, RequestChangingParkParameters> requests) {
        try {
            String tableName = this.schemaName + ".requeststodepmanager";
            for (RequestChangingParkParameters req : requests.values()) {
                String columns = "ParkID, DepartmentID, parameter, requestedValue, status";
                if (!dbController.insertRecord(tableName, columns, "'" + req.getPark().getParkID() + "'",
                        String.valueOf(req.getPark().getDepartment()),
                        String.valueOf(req.getParameter().getParameterVal()),
                        String.valueOf(req.getRequestedValue()),
                        String.valueOf(req.getStatus().getRequestStatus()))) {
                    this.serverController.addtolog("Insert into " + tableName + " failed. Insert request:" + req);
                    return false;
                }
            }
            return true;
        } catch (SQLException e) {
            this.serverController.addtolog(e.getMessage());
            return false;
        }
    }

    // =================================================================================================================//
    //                                                                                                                 //
    //                                           WORKER EXCLUSIVE METHODS                                              //
    //                                                                                                                 //
    //=================================================================================================================//
    public void updateOrderStatusForUpcomingVisits() {
        try {
            ArrayList<ArrayList<String>> Orders = new ArrayList<>();
            String tableName = this.schemaName + ".orders";
            String whereClause = "VisitationDate BETWEEN NOW() + INTERVAL 23 HOUR + INTERVAL 59 MINUTE AND NOW() + INTERVAL 24 HOUR + INTERVAL 1 MINUTE AND orderStatus = " + (OrderStatus.STATUS_ACCEPTED.ordinal() + 1);
            try {
                ResultSet rs = dbController.selectRecordsFields(tableName, whereClause, "OrderID", "ClientEmailAddress");
                while (rs.next()) {
                    Orders.add(new ArrayList<>());
                    Orders.get(Orders.size() - 1).add(rs.getString("OrderID"));
                    Orders.get(Orders.size() - 1).add(rs.getString("ClientEmailAddress"));
                }
            } catch (SQLException e) {
                serverController.addtolog("Select upcoming orders failed: " + e.getMessage());
                return;
            }

            // Update the status of selected orders to pending confirmation
            if (!Orders.isEmpty()) {
                for (ArrayList<String> order : Orders) {
                    if (!updateOrderStatus(order.get(0), OrderStatus.STATUS_PENDING_CONFIRMATION)) {
                        serverController.addtolog("Failed to update order status for OrderID: " + order.get(0));
                    } else {
                        serverController.addtolog("Send to Email Address: " + order.get(1) + " Cancelled");
                    }
                }
            }
        } catch (Exception e) {
            serverController.addtolog("Error updating order status for upcoming visits: " + e.getMessage());
        }
    }

    public void ChangeLatePendingConfirmationToCancelled() {
        try {
            ArrayList<ArrayList<String>> Orders = new ArrayList<>();
            String tableName = this.schemaName + ".orders";
            String whereClause = "VisitationDate BETWEEN NOW() + INTERVAL 79199 SECOND AND NOW() + INTERVAL 79260 SECOND AND orderStatus = " + (OrderStatus.STATUS_PENDING_CONFIRMATION.getOrderStatus());
            try {
                ResultSet rs = dbController.selectRecordsFields(tableName, whereClause, "OrderID", "ClientEmailAddress");
                while (rs.next()) {
                    Orders.add(new ArrayList<>());
                    Orders.get(Orders.size() - 1).add(rs.getString("OrderID"));
                    Orders.get(Orders.size() - 1).add(rs.getString("ClientEmailAddress"));
                }
            } catch (SQLException e) {
                serverController.addtolog("Select upcoming orders failed: " + e.getMessage());
                return;
            }

            // Update the status of selected orders to cancelled
            if (!Orders.isEmpty()) {
                for (ArrayList<String> order : Orders) {
                    if (!updateOrderStatus(order.get(0), OrderStatus.STATUS_CANCELLED)) {
                        serverController.addtolog("Failed to cancel order status for OrderID: " + order.get(0));
                    } else {
                        serverController.addtolog("Send to Email Address: " + order.get(1) + " Confirm notification");
                    }
                }
            }
        } catch (Exception e) {
            serverController.addtolog("Error updating order status for upcoming visits: " + e.getMessage());
        }
    }

    public int registerGroupGuide(String newGroupGuideID) {
        try {
            String visitorsTableName = this.schemaName + ".visitors";
            ResultSet resultFromVisitors = dbController.selectRecords(visitorsTableName, "VisitorID='" + newGroupGuideID + "'");
            if (!resultFromVisitors.next())
                return 0; // id doesnt exist.
            String username = resultFromVisitors.getString("username");
            String usersTableName = this.schemaName + ".users";
            ResultSet resultFromUsers = dbController.selectRecords(usersTableName, "username='" + username + "' AND role='" + Role.ROLE_VISITOR_GROUP_GUIDE + "'");
            if (resultFromUsers.next())
                return 1; // user is already a group guide.
            if (dbController.updateRecord(usersTableName, "role=" + Role.ROLE_VISITOR_GROUP_GUIDE, "username='" + username + "'"))
                return 2; // success
            return -1; // failure
        } catch (SQLException e) {
            this.serverController.addtolog(e.getMessage());
            return -1; // error
        }
    }

    /**
     * Sets the exit time of an order to now.
     *
     * @param orderID the order ID.
     * @return a message indicating the result of the operation, null if successful.
     */
    public String setExitTimeOfOrder(String orderID) {
        try {
            String tableName = this.schemaName + ".orders";
            String whereClause = "OrderID='" + orderID + "'";
            ResultSet resultSet = dbController.selectRecords(tableName, whereClause);
            if (!resultSet.next())
                return "Order id doesn`t exist.";
            if (resultSet.getTimestamp("ExitedTime") != null)
                return "Order has already exited.";

            if (!dbController.updateRecord(tableName, "ExitedTime=CURRENT_TIMESTAMP()", whereClause))
                return "failed exiting, please try again.";
            return null;
        } catch (SQLException e) {
            this.serverController.addtolog(e.getMessage());
            return "Exiting failed to unknown reason, please try again later.";
        }
    }
}
