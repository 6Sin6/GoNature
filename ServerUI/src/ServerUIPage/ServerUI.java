package ServerUIPage;

import Server.GoNatureServer;
import ServerUIPageController.ServerPortFrameController;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;


public class ServerUI extends Application {

    public static void main(String args[]) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ServerPortFrameController aFrame = new ServerPortFrameController();
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
