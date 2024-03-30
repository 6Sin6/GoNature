package CommonClient.controllers;

import CommonClient.ClientUI;
import Entities.Message;
import Entities.OpCodes;
import client.ClientCommunicator;
import client.ClientController;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * This class represents the controller for the Connect Client Page in a JavaFX application. It extends a base controller
 * class and manages the user interface and interactions for connecting to a server.
 */
public class ConnectClientPageController extends BaseController {

    /**
     * Button to initiate the connection process.
     */
    @FXML
    private MFXButton connectBtn;

    /**
     * Label to display error messages during connection attempts.
     */
    @FXML
    private Label lblErrorMsg;

    /**
     * Text field for inputting the server IP address.
     */
    @FXML
    private TextField ipTxt;

    /**
     * Static variable to keep track of the current IP address.
     * Initially set to "localhost".
     */
    private static String CurrentIP = "localhost";

    /**
     * Method called when the connect button is clicked. It attempts to build
     * a connection to the server using the IP address provided in the {@code ipTxt}
     * text field and performs a handshake. If successful, it navigates to the
     * home page of the application. If any step fails, it displays an error message.
     */
    public void onConnect() {
        try {
            buildConnection();
            performHandshake();
            applicationWindowController.setCenterPage("/CommonClient/gui/HomePage.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            lblErrorMsg.setText("Something is wrong, try again, please!");
        }
    }

    /**
     * Clears the IP address input field. This method is typically called during
     * cleanup or preparation for a new connection attempt.
     */
    public void cleanup() {
        ipTxt.clear();
    }

    /**
     * Attempts to establish a new connection to the server using the IP address
     * and port number specified. If a connection already exists, this method does
     * nothing. Otherwise, it updates the current IP address and creates a new
     * {@link ClientController} instance for communication.
     */
    private void buildConnection() {
        if (ClientUI.client == null) {
            System.out.println("Connecting to " + ipTxt.getText() + " on port 5555...");
            CurrentIP = ipTxt.getText();
            ClientUI.client = new ClientController(ipTxt.getText(), 5555);
        }
    }

    /**
     * Sends a handshake message to the server to confirm connectivity. If the
     * server responds with an expected handshake opcode, the method completes
     * successfully. If the server is not running or does not respond as expected,
     * the application will exit.
     */
    private void performHandshake() {
        Message msg = new Message(OpCodes.OP_SYNC_HANDSHAKE);
        ClientUI.client.accept(msg);
        if (ClientCommunicator.msg.getMsgOpcode() == OpCodes.OP_SYNC_HANDSHAKE) {
            return;
        }
        System.exit(0); // Server is not running
    }

}