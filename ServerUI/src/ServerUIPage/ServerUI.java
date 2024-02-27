package ServerUIPage;

import GoNatureServer.GoNatureServer;
import ServerUIPageController.ServerPortFrameController;
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
        ServerPortFrameController aFrame = new ServerPortFrameController();
        primaryStage.setOnCloseRequest(e -> Platform.runLater(()-> {
            try {
                GoNatureServer.closeServer();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }));
        aFrame.start(primaryStage);
    }

    public static void runServer(ServerPortFrameController guiController) {
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

    public static void closeServer() throws IOException {
        GoNatureServer.closeServer();
    }
}
