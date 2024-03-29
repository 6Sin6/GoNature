package GoNatureServer;

import DataBase.DBConnection;
import DataBase.DBController;
import ServerUIPageController.ServerUIFrameController;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class is responsible for simulating the import of users from an external database.
 * It handles the process of retrieving user data and inserting it into the correct tables in the local database.
 */
public class ImportSimulator {
    /**
     * The controller for the server user interface.
     */
    private ServerUIFrameController serverController;

    /**
     * The connection to the database.
     */
    private DBConnection dbConnection;

    /**
     * The controller for the database.
     */
    private DBController dbController;

    /**
     * The name of the schema in the external database.
     */
    private final String schemaName = "usermanagement";

    /**
     * Constructor for the ImportSimulator class.
     *
     * @param controller   The controller for the server user interface.
     * @param dbConnection The connection to the database.
     */
    public ImportSimulator(ServerUIFrameController controller, DBConnection dbConnection) {
        this.serverController = controller;
        this.dbController = dbConnection.getDbController();
        this.dbConnection = dbConnection;
    }

    /**
     * This method handles the import of users from the external database.
     * It retrieves all user data and inserts it into the correct tables in the local database.
     * All this based on the role of the user.
     * @throws Exception If an error occurs while importing users.
     */
    public void handleImportUsers() throws Exception {
        try {
            String tableName = schemaName + ".users";
            ResultSet allUsers = dbController.selectRecords(tableName, "");
            while (allUsers.next()) {
                String username = allUsers.getString("username");
                String password = allUsers.getString("password");
                int role = allUsers.getInt("role");
                String email = allUsers.getString("emailAddress");
                String firstName = allUsers.getString("firstname");
                String lastName = allUsers.getString("lastname");
                String id = allUsers.getString("id");
                this.dbConnection.insertUser(username, password, role);

                switch (role) {
                    case 1:
                        this.dbConnection.insertGroupGuide(username, id, email, firstName, lastName);
                        break;
                    case 2:
                    case 4:
                    case 5:
                        ResultSet employeeInfo = dbController.selectRecords(schemaName + ".organization_roles", "username='" + username + "'");
                        if (employeeInfo.next()) {
                            String parkID = employeeInfo.getString("parkID");
                            boolean isParkManager = employeeInfo.getBoolean("isParkManager");
                            this.dbConnection.insertParkEmployee(username, email, parkID, firstName, lastName, isParkManager, id);
                        }
                        break;
                    case 3:
                        ResultSet departmentManagerInfo = dbController.selectRecords(schemaName + ".organization_roles", "username='" + username + "'");
                        if (departmentManagerInfo.next()) {
                            String departmentID = departmentManagerInfo.getString("departmentID");
                            this.dbConnection.insertDepartmentManager(username, email, departmentID, firstName, lastName, id);
                        }
                        break;

                }
            }
            allUsers.beforeFirst();
        } catch (SQLException e) {
            serverController.addtolog("Error in importing users: " + e.getMessage());
            throw e;
        }
    }
}
