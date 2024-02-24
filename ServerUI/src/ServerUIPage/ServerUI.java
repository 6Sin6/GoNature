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
        int port = 0; //Port to listen on
        try {
            port = Integer.parseInt(guiController.getPort()); //Set port to 5555

        } catch (Throwable t) {
            System.out.println("ERROR - Could not connect!");
        }
        try {

            GoNatureServer sv = GoNatureServer.getInstance(port, guiController);
            try {
                sv.listen(); //Start listening for connections
            } catch (Exception ex) {
                guiController.addtolog("ERROR - Could not listen for clients!");
            }

        } catch (Exception e) {
            guiController.addtolog(e.getMessage());
        }
    }
    public static void closeServer() throws IOException {
        GoNatureServer.closeServer();
    }



}
