package CommonClient;


import CommonClient.controllers.ApplicationWindowController;
import client.ClientController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;


public class ClientUI extends Application {
    /**
     * A static instance of {@link ClientController} used across the application for managing network communication
     * with the server. This instance facilitates sending requests to and receiving responses from the server,
     * enabling the application to interact with server-side functionalities.
     * <p>
     * The {@code client} instance is meant to be initialized once and reused throughout the application's lifecycle
     * to maintain a consistent communication channel. It's utilized by various components of the application to
     * perform operations such as user authentication, data retrieval, and any other server-required interactions.
     */
    public static ClientController client;

    public static void main(String[] args) throws Exception {
        launch(args);
    }


    /**
     * Initializes the primary stage of the JavaFX application, setting up the main application frame
     * and defining the close request behavior. This method is called as the entry point for the JavaFX
     * application, setting up the initial view and ensuring proper shutdown procedures are followed.
     *
     * @param primaryStage The primary stage for this application, onto which the application scene can be set.
     *                     The stage is passed by the JavaFX framework during the start-up phase.
     * @throws Exception Throws an exception if the application initialization fails for any reason.
     *                   <p>
     *                   The method initializes an {@link ApplicationWindowController} instance and uses it to start the application
     *                   frame. It also sets a close request handler on the primary stage to ensure that when the application window
     *                   is closed, any necessary cleanup is performed. Specifically, it attempts to gracefully quit the client connection
     *                   to the server. If the server has not been initialized, it catches the resulting exception and prints a message
     *                   indicating the server was not initialized, preventing the application from crashing on shutdown.
     */
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
