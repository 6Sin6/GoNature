package DataBase;

import Entities.*;
import ServerUIPageController.ServerPortFrameController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
     * Sets up the database connection using the provided URL, user name, and password.
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

    public User login(String username, String password) {
        try {
            String tableName = this.schemaName + ".users";
            String whereClause = "username='" + username + "' AND password='" + password + "'";
            ResultSet results = dbController.selectRecords(tableName, whereClause);

            if (results.next()) {
                return new User(
                        results.getString("username"),
                        results.getString("password"),
                        Role.values()[results.getInt("role")]
                );
            }
            return null;
        } catch (SQLException e) {
            this.serverController.addtolog(e.getMessage());
            return null;
        }
    }

    public boolean addOrder(Order order) {
        try {
            String tableName = this.schemaName + ".orders";
            String columns = "VisitorID, ParkID, VisitationDate, ClientEmailAddress, PhoneNumber, OrderStatus, EnteredTime, ExitedTime, OrderID, OrderType, NumOfVisitors";
            String values = "'"
                            + order.getVisitorID() + "', '"
                            + order.getParkID() + "', '"
                            + order.getVisitationDate() + "', '"
                            + order.getClientEmailAddress() + "', '"
                            + order.getPhoneNumber() + "', "
                            + order.getOrderStatus().ordinal() + ", '"
                            + order.getEnteredTime() + "', '"
                            + order.getExitedTime() + "', '"
                            + order.getOrderID() + "', "
                            + order.getOrderType().ordinal() + ", "
                            + order.getNumOfVisitors();
            if (!dbController.insertRecord(tableName, columns, values)) {
                this.serverController.addtolog("Insert into " + tableName + " failed. Insert order:" + order);
                return false;
            }
            this.serverController.addtolog("Insert into " + tableName + " succeeded. Insert order:" + order);
            return true;
        } catch (SQLException e) {
            this.serverController.addtolog(e.getMessage());
            return false;
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
                        OrderStatus.values()[results.getInt("orderStatus")],
                        results.getTimestamp("EnteredTime"),
                        results.getTimestamp("ExitedTime"),
                        results.getString("OrderID"),
                        OrderType.values()[results.getInt("OrderType")],
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
                        OrderStatus.values()[results.getInt("orderStatus")],
                        results.getTimestamp("EnteredTime"),
                        results.getTimestamp("ExitedTime"),
                        results.getString("OrderID"),
                        OrderType.values()[results.getInt("OrderType")],
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
            String setClause = "OrderStatus=" + status.ordinal();
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

    public boolean updateOrderEmailAddress(String orderID, String emailAddress) {
        try {
            String tableName = this.schemaName + ".orders";
            String setClause = "ClientEmailAddress=" + emailAddress;
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

    public boolean updateOrderStatusAsCancelled(String orderID) {
        return updateOrderStatus(orderID, OrderStatus.STATUS_CANCELLED);
    }

    public Park getParkDetails(String parkID) {
        try {
            String tableName = this.schemaName + ".parks";
            String whereClause = "ParkID=" + parkID;
            ResultSet results = dbController.selectRecords(tableName, whereClause);
            if (results.next()) {
                String pManagerId = results.getString("ParkManagerID");
                ResultSet managerResults = dbController.selectRecords(this.schemaName + ".parkemployees", "id=" + pManagerId);
                if (managerResults.next()) {
                    return new Park(
                            results.getString("ParkID"),
                            results.getString("ParkName"),
                            results.getInt("Capacity"),
                            results.getInt("GapVisitorsCapacity"),
                            results.getTimestamp("DefaultVisitationTime"),
                            results.getInt("Department"),
                            new ParkManager(
                                    managerResults.getString("username"),
                                    "",
                                    managerResults.getString("EmailAddress"),
                                    null // Todo: fix this two-way dependency shit.
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
}
