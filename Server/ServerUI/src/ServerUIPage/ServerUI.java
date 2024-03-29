package ServerUIPage;

import DataBase.DBConnection;
import GoNatureServer.GoNatureServer;
import GoNatureServer.ImportSimulator;
import ServerUIPageController.ServerUIFrameController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;


/**
 * This class represents the server user interface of the application.
 * It extends the Application class from the JavaFX library.
 */
public class ServerUI extends Application {

    /**
     * The main method that launches the application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * The start method that is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * @param primaryStage The primary stage for this application.
     * @throws Exception If an error occurs while starting the application.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        ServerUIFrameController aFrame = new ServerUIFrameController();
        primaryStage.setOnCloseRequest(e -> Platform.runLater(() -> {
            GoNatureServer.closeServer();
        }));
        aFrame.start(primaryStage);
    }

    /**
     * This method runs the server.
     *
     * @param guiController The GUI controller for the server.
     */
    public static void runServer(ServerUIFrameController guiController) {
        int port = 5555; // Fallback port

        try {
            port = Integer.parseInt(guiController.getPort());
        } catch (Throwable t) {
            System.out.println("Failed to connect on requested port. Using fallback port 5555");
        }

        try {
            GoNatureServer sv = GoNatureServer.getInstance(port, guiController);
            sv.initializeDBConnection(guiController); // For reinitializing the DB connection if it was disconnected.
            try {
                sv.listen();
            } catch (Exception ex) {
                guiController.addtolog("Error: Failed to listen for clients!");
            }
        } catch (Exception e) {
            guiController.addtolog(e.getMessage());
        }
    }

    /**
     * This method initializes the import simulator.
     *
     * @param guiController The GUI controller for the server.
     * @throws Exception If an error occurs while initializing the import simulator.
     */
    public static void initializeImportSimulator(ServerUIFrameController guiController) throws Exception {
        try {
            GoNatureServer server = GoNatureServer.getInstance(5555, guiController);
            DBConnection db = server.getDBConnection();
            ImportSimulator simulator = new ImportSimulator(guiController, db);
            simulator.handleImportUsers();
        } catch (Exception e) {
            guiController.addtolog(e.getMessage());
            throw e;
        }
    }


    /**
     * This method checks the availability of users.
     *
     * @param guiController The GUI controller for the server.
     * @return A boolean indicating whether users are available.
     */
    public static boolean checkUsersAvailability(ServerUIFrameController guiController) {
        try {
            GoNatureServer server = GoNatureServer.getInstance(5555, guiController);
            DBConnection db = server.getDBConnection();
            boolean usersAvailability = db.checkUsersAvailability();
            return usersAvailability;
        } catch (Exception e) {
            guiController.addtolog(e.getMessage());
            return false;
        }
    }

    /**
     * This method closes the server.
     */
    public static void closeServer() {
        GoNatureServer.closeServer();
    }
}
