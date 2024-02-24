package DataBase;

import Entities.Order;
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
            this.conn = DriverManager.getConnection("jdbc:mysql://" + url + ":3306/test?serverTimezone=IST&useSSL=false&allowPublicKeyRetrieval=true", user, password);
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

    public void insertRecord(String tableName, String... values) {
        try {
            if (!dbController.insertRecord(tableName, values)) {
                this.serverController.addtolog("Insert into " + tableName + " failed");
            } else {
                this.serverController.addtolog("Insert into " + tableName + " succeed");
                this.serverController.addtolog("Inserted record: " + String.join(", ", values));
            }
        } catch (SQLException e) {
            this.serverController.addtolog(e.getMessage());
        }
    }

    public ArrayList<Order> getOrders() {
        try {
            ResultSet results = dbController.selectRecords("", "");
            return new ArrayList<>();
        } catch (SQLException e) {
            this.serverController.addtolog(e.getMessage());
            return null;
        }
    }

    public Order getOrderById(String orderId) {
        try {
            ResultSet results = dbController.selectRecords("", "");
            return (Order) results;
        } catch (SQLException e) {
            this.serverController.addtolog(e.getMessage());
            return null;
        }
    }

    public boolean updateOrderById(Order updatedOrder) {
        try {
            ResultSet results = dbController.selectRecords("", "");
            return true;
        } catch (SQLException e) {
            this.serverController.addtolog(e.getMessage());
            return false;
        }
    }
}
