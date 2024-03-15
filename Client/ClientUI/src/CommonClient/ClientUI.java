package CommonClient;


import CommonClient.controllers.ApplicationWindowController;
import client.ClientController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;


public class ClientUI extends Application {
    public static ClientController client;

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ApplicationWindowController appFrame = new ApplicationWindowController();
        primaryStage.setOnCloseRequest(e -> Platform.runLater(() -> {
            try {
                client.quit();
            } catch (Exception ex) {
                System.out.println("Server didnt initialized");
            }
        }));
        appFrame.start(primaryStage);
    }
}
