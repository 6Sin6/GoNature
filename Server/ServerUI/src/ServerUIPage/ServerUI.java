package ServerUIPage;

import DataBase.DBConnection;
import GoNatureServer.GoNatureServer;
import GoNatureServer.ImportSimulator;
import ServerUIPageController.ServerUIFrameController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;


public class ServerUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ServerUIFrameController aFrame = new ServerUIFrameController();
        primaryStage.setOnCloseRequest(e -> Platform.runLater(() -> {
            GoNatureServer.closeServer();
        }));
        aFrame.start(primaryStage);
    }

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

    public static void initializeImportSimulator(ServerUIFrameController guiController) throws Exception {
        try {
            GoNatureServer server = GoNatureServer.getInstance(5555, guiController);
            DBConnection db = server.getDBConnection(guiController);
            ImportSimulator simulator = new ImportSimulator(guiController, db);
            simulator.handleImportUsers();
        } catch (Exception e) {
            guiController.addtolog(e.getMessage());
            throw e;
        }
    }


    public static boolean checkUsersAvailability(ServerUIFrameController guiController) {
        try {
            GoNatureServer server = GoNatureServer.getInstance(5555, guiController);
            DBConnection db = server.getDBConnection(guiController);
            boolean usersAvailability = db.checkUsersAvailability();
            return usersAvailability;
        } catch (Exception e) {
            guiController.addtolog(e.getMessage());
            return false;
        }
    }

    public static void closeServer() throws IOException {
        GoNatureServer.closeServer();
    }
}
