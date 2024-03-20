package DataBase;

import Entities.*;
import GoNatureServer.GoNatureServer;
import GoNatureServer.GmailSender;
import ServerUIPageController.ServerPortFrameController;




import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static CommonUtils.CommonUtils.convertMinutesToTimestamp;
import static CommonUtils.CommonUtils.convertTimestampToMinutes;

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

    /**
     * Logs in a user with the provided username and password.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @return A User object representing the logged-in user, or null if login fails or there is an error.
     */
    public User login(String username, String password) {
        try {
            String tableName = this.schemaName + ".users";
            String whereClause = "username='" + username + "' AND password='" + password + "'";
            ResultSet userCredentials = dbController.selectRecords(tableName, whereClause);

            if (userCredentials.next()) {
                int userRole = userCredentials.getInt("role");
                String userTypeTableName = userRole < 2 ? ".group_guides" : userRole == 3 ? ".department_managers" : ".park_employees";
                ResultSet userGoNatureData =
                        dbController.selectRecords(this.schemaName + userTypeTableName, "username='" + username + "'");

                if (userGoNatureData.next()) {
                    switch (userRole) {
                        case 1:
                            return new VisitorGroupGuide(
                                    userCredentials.getString("username"),
                                    "",
                                    userGoNatureData.getString("emailAddress"),
                                    userGoNatureData.getString("ID"),
                                    userGoNatureData.getString("firstName"),
                                    userGoNatureData.getString("lastName")
                            );
                        case 2:
                            ResultSet parkData = dbController.selectRecords(this.schemaName + ".parks", "ParkID=" + userGoNatureData.getString("ParkID"));
                            ResultSet managerData = dbController.selectRecords(this.schemaName + ".park_employees", "ParkID=" + userGoNatureData.getString("ParkID") + " AND isParkManager=true");
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
                                                        parkData.getString("ParkID"),
                                                        managerData.getString("firstName"),
                                                        managerData.getString("lastName")
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
                                    userGoNatureData.getInt("departmentID"),
                                    userGoNatureData.getString("firstName"),
                                    userGoNatureData.getString("lastName")
                            );
                        case 4:
                            return new ParkManager(
                                    userCredentials.getString("username"),
                                    "",
                                    userGoNatureData.getString("EmailAddress"),
                                    userGoNatureData.getString("ParkID"),
                                    userGoNatureData.getString("firstName"),
                                    userGoNatureData.getString("lastName")
                            );
                        case 5:
                            ResultSet parkDataSupport = dbController.selectRecords(this.schemaName + ".parks", "ParkID=" + userGoNatureData.getString("ParkID"));
                            ResultSet parkManagerData = dbController.selectRecords(this.schemaName + ".park_employees", "ParkID=" + userGoNatureData.getString("ParkID") + " AND isParkManager=true");
                            if (parkDataSupport.next() && parkManagerData.next()) {
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
                                                        parkManagerData.getString("username"),
                                                        "",
                                                        parkManagerData.getString("EmailAddress"),
                                                        parkDataSupport.getString("ParkID"),
                                                        parkManagerData.getString("firstName"),
                                                        parkManagerData.getString("lastName")

                                                )
                                        ),
                                        userCredentials.getString("firstName"),
                                        userCredentials.getString("lastName")
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

    /**
     * Retrieves an order by user ID and order ID.
     *
     * @param userID  The ID of the user.
     * @param orderID The ID of the order.
     * @return The order associated with the specified user ID and order ID, or null if not found or there is an error.
     */
    public Order getUserOrderByUserID(String userID, String orderID) {
        try {
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
            return null;
        } catch (SQLException e) {
            this.serverController.addtolog(e.getMessage());
            return null;
        }
    }

    /**
     * Adds an order to the database.
     *
     * @param order The order to be added.
     * @return The added order with the assigned order ID, or null if the insertion fails or there is an error.
     */
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
            ResultSet results = dbController.selectRecordsFields(tableName, "VisitorID='" + order.getVisitorID() + "' AND ParkID='" + order.getParkID() + "' AND VisitationDate='" + order.getVisitationDate() + "'", "OrderID");
            if (results.next()) {
                order.setOrderID(results.getString("OrderID"));
                return order;
            }
            return null;
        } catch (SQLException e) {
            this.serverController.addtolog(e.getMessage());
            return null;
        }
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param OrderID The ID of the order to retrieve.
     * @return The order with the specified ID, or null if the order is not found or there is an error.
     */
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

    /**
     * Retrieves all orders associated with a specific visitor.
     *
     * @param visitorID The ID of the visitor.
     * @return An ArrayList containing orders associated with the specified visitor, or null if there is an error.
     */
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

    /**
     * Updates the status of an order.
     *
     * @param orderID The ID of the order to be updated.
     * @param status  The new status of the order.
     * @return True if the order status is successfully updated, false otherwise.
     */
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

    /**
     * Updates the details of an order.
     *
     * @param details An array containing the order details in the following format: [orderID, phoneNumber, clientEmailAddress].
     * @return True if the order details are successfully updated, false otherwise.
     */
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

    /**
     * Updates the status of an order to "cancelled".
     *
     * @param orderID The ID of the order to be updated.
     * @return True if the order status is successfully updated to "cancelled", false otherwise.
     */
    public boolean updateOrderStatusAsCancelled(String orderID) {
        return updateOrderStatus(orderID, OrderStatus.STATUS_CANCELLED);
    }

    /**
     * Checks if an order exists for a specific visitor, park, and visitation date.
     *
     * @param visitorID       The ID of the visitor.
     * @param parkID          The ID of the park.
     * @param visitationDate  The visitation date.
     * @return True if an order exists for the specified visitor, park, and visitation date, false otherwise.
     */
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
     *         Returns a new Park object with default values if no matching park is found.
     *         Returns null if a SQLException is thrown.
     */
    public Park getParkDetails(String parkID) {
        try {
            String tableName = this.schemaName + ".parks";
            String whereClause = "ParkID=" + parkID;
            ResultSet results = dbController.selectRecords(tableName, whereClause);
            if (results.next()) {
                String pManagerId = results.getString("ParkManagerID");
                ResultSet managerResults = dbController.selectRecords(this.schemaName + ".park_employees", "id=" + pManagerId);
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
                                    results.getString("ParkID"),
                                    managerResults.getString("firstName"),
                                    managerResults.getString("lastName")
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

    /**
     * Marks an order as paid by inserting a record into the payments table and updating the order status.
     *
     * @param order The order to be marked as paid.
     * @return True if the order is successfully marked as paid, false otherwise.
     */
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
            String columns = "OrderID, paid, price";

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

    /**
     * Retrieves requests from a park manager for a specific department.
     *
     * @param departmentID The ID of the department.
     * @return An ArrayList of requests from the park manager, or null if there is an error.
     */
    public ArrayList<RequestChangingParkParameters> getRequestsFromParkManager(Integer departmentID) {
        try {
            String tableName = this.schemaName + ".park_parameters_requests";
            String whereClause = "DepartmentID=" + departmentID + " AND status=" + RequestStatus.REQUEST_PENDING.getRequestStatus();
            ResultSet results = dbController.selectRecords(tableName, whereClause);
            ArrayList<RequestChangingParkParameters> requests = new ArrayList<>();
            while (results.next()) {
                ResultSet parkResults = dbController.selectRecords(this.schemaName + ".parks", "ParkID=" + results.getString("ParkID"));
                if (parkResults.next()) {
                    ResultSet parkManagerResults = dbController.selectRecords(this.schemaName + ".park_employees", "id=" + parkResults.getString("ParkManagerID") + " AND isParkManager=true");
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
                                                parkManagerResults.getString("ParkID"),
                                                parkManagerResults.getString("firstName"),
                                                parkManagerResults.getString("lastName")
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

    /**
     * Authorizes a park request, updating the park parameters and request status in the database.
     *
     * @param req The request to authorize, containing details about the park, parameter, and requested value.
     * @return {@code true} if the authorization process succeeds, {@code false} otherwise.
     */
    public boolean authorizeParkRequest(RequestChangingParkParameters req) {
        try {
            String tableName = this.schemaName + ".park_parameters_requests";
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

    /**
     * Unauthorizes a park parameter change request.
     *
     * @param req The request to be unauthorized.
     * @return {@code true} if the request is successfully unauthorized, {@code false} otherwise.
     */
    public boolean unauthorizeParkRequest(RequestChangingParkParameters req) {
        try {
            String tableName = this.schemaName + ".park_parameters_requests";
            String setClause = "Status=" + RequestStatus.REQUEST_DECLINED.getRequestStatus() + ", handleDate=CURRENT_TIMESTAMP()";
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

    /**
     * Submits requests to the department for changing park parameters.
     *
     * @param requests A map containing the park parameters to be changed and their corresponding request objects.
     * @return {@code true} if all requests were successfully submitted, {@code false} otherwise.
     */
    public boolean submitRequestsToDepartment(Map<ParkParameters, RequestChangingParkParameters> requests) {
        try {
            String tableName = this.schemaName + ".park_parameters_requests";
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

    /**
     * Registers a user as a group guide.
     *
     * @param newGroupGuideID The ID of the user to be registered as a group guide.
     * @return 0 if the provided ID doesn't exist, 1 if the user is already a group guide,
     *         2 if the registration is successful, -1 if an error occurs.
     */
    public int registerGroupGuide(String newGroupGuideID) {
        try {
            String guidesTableName = this.schemaName + ".group_guides";
            ResultSet resultFromGuides = dbController.selectRecordsFields(guidesTableName, "ID='" + newGroupGuideID + "'", "username");
            if (!resultFromGuides.next())
                return 0; // id doesnt exist.
            String username = resultFromGuides.getString("username");
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
    public String setExitTimeOfOrder(String orderID)
    {
        try {
            String tableName = this.schemaName + ".orders";
            String whereClause = "OrderID='" + orderID + "'";
            ResultSet resultSet = dbController.selectRecordsFields(tableName, whereClause, "ExitedTime");
            if (!resultSet.next())
                return "Order id doesn`t exist.";
            if (resultSet.getTimestamp("ExitedTime") != null)
                return  "Order has already exited.";

            if (!dbController.updateRecord(tableName, "ExitedTime=CURRENT_TIMESTAMP()", whereClause))
                return "failed exiting, please try again.";
            return null;
        } catch (SQLException e) {
            this.serverController.addtolog(e.getMessage());
            return "Exiting failed to unknown reason, please try again later.";
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
            String whereClause = "VisitationDate BETWEEN NOW() + INTERVAL 23 HOUR + INTERVAL 59 MINUTE AND NOW() + INTERVAL 24 HOUR + INTERVAL 1 MINUTE AND orderStatus = " + (OrderStatus.STATUS_ACCEPTED.getOrderStatus());
            try {
                ResultSet rs = dbController.selectRecordsFields(tableName, whereClause, "OrderID", "ClientEmailAddress", "VisitorID");
                while (rs.next()) {
                    Orders.add(new ArrayList<>());
                    Orders.get(Orders.size() - 1).add(rs.getString("OrderID"));
                    Orders.get(Orders.size() - 1).add(rs.getString("ClientEmailAddress"));
                    Orders.get(Orders.size() - 1).add(rs.getString("VisitorID"));

                }
            } catch (SQLException e) {
                serverController.addtolog("Select upcoming orders failed: " + e.getMessage());
                return;
            }

            // Update the status of selected orders to pending confirmation
            if (!Orders.isEmpty()) {
                ArrayList<ArrayList<String>> PendingConfirmationOrders = new ArrayList<>();
                for (ArrayList<String> order : Orders) {
                    if (!updateOrderStatus(order.get(0), OrderStatus.STATUS_PENDING_CONFIRMATION)) {
                        serverController.addtolog("Failed to update order status for OrderID: " + order.get(0));
                    }
                    else{
                        PendingConfirmationOrders.add(order);
                    }
                }
                sendMails(PendingConfirmationOrders,"Order Confirmation Notification","awaiting confirmation");
            }
        } catch (Exception e) {
            serverController.addtolog("Error updating order status for upcoming visits: " + e.getMessage());
        }
    }

    public void ChangeLatePendingConfirmationToCancelled() {
        try {
            ArrayList<ArrayList<String>> Orders = new ArrayList<>();
            String tableName = this.schemaName + ".orders";
            String whereClause = "VisitationDate BETWEEN NOW() + INTERVAL 79199 SECOND AND NOW() + INTERVAL 79320 SECOND AND orderStatus = " + (OrderStatus.STATUS_PENDING_CONFIRMATION.getOrderStatus());
            try {
                ResultSet rs = dbController.selectRecordsFields(tableName, whereClause, "OrderID", "ClientEmailAddress","VisitorID");
                while (rs.next()) {
                    Orders.add(new ArrayList<>());
                    Orders.get(Orders.size() - 1).add(rs.getString("OrderID"));
                    Orders.get(Orders.size() - 1).add(rs.getString("ClientEmailAddress"));
                    Orders.get(Orders.size() - 1).add(rs.getString("VisitorID"));
                }
            } catch (SQLException e) {
                serverController.addtolog("Select upcoming orders failed: " + e.getMessage());
                return;
            }

            // Update the status of selected orders to cancelled
            if (!Orders.isEmpty()) {
                ArrayList<ArrayList<String>> CancelledOrders = new ArrayList<>();
                for (ArrayList<String> order : Orders) {
                    if (!updateOrderStatus(order.get(0), OrderStatus.STATUS_CANCELLED)) {
                        serverController.addtolog("Failed to cancel order status for OrderID: " + order.get(0));
                    } else {
                        CancelledOrders.add(order);
                    }
                }
                sendMails(CancelledOrders,"Order Cancelled Notification","cancelled");
            }
        } catch (Exception e) {
            serverController.addtolog("Error updating order status for upcoming visits: " + e.getMessage());
        }
    }

    private void sendMails(ArrayList<ArrayList<String>> Orders,String Subject,String Type){
        try{
            new Thread(() -> {
                for (ArrayList<String> order : Orders) {
                    GmailSender.sendEmail(order.get(1),Subject,"Hello Visitor " + order.get(2)+"\n"+"Your order id : "+order.get(0)+" is now "+Type);
                }
            }).start();
        }
        catch (Exception e){
            serverController.addtolog("Error sending email: " + e.getMessage());
        }
    }
}