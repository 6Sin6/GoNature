package EmployeesControllers;

import CommonClient.ClientUI;
import CommonClient.controllers.BaseController;
import CommonUtils.CommonUtils;
import CommonUtils.ConfirmationPopup;
import Entities.Message;
import Entities.OpCodes;
import client.ClientCommunicator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class RegisterGroupGuideController extends BaseController {

    /**
     * Represents a JavaFX label used for displaying general error messages.
     */
    @FXML
    private Label lblErrorMsgGeneral;

    /**
     * Represents a JavaFX label used for displaying error messages related to the ID field.
     */
    @FXML
    private Label lblErrorMsgID;

    /**
     * Represents a JavaFX text field used for entering an ID.
     */
    @FXML
    private TextField txtID;


    /**
     * Resets the text fields and error message labels to their default states.
     * This method clears the text in the ID text field and resets the error messages displayed in two error message labels.
     * It is typically used to clean up the UI after a form submission or when resetting the input fields.
     */
    public void cleanup() {
        txtID.setText("");
        lblErrorMsgGeneral.setText("");
        lblErrorMsgID.setText("");
    }

    /**
     * Checks whether the provided ID is valid.
     *
     * @param id The ID to be validated.
     * @return {@code true} if the ID is valid, {@code false} otherwise.
     */
    private boolean isDetailsValid(String id) {
        return (CommonUtils.isValidID(id));
    }

    /**
     * Retrieves the ID entered in a text field.
     *
     * @return The ID entered in the associated text field.
     */
    private String getID() {
        return txtID.getText();
    }

    /**
     * Handles the action event triggered by clicking the "SubmitButton".
     * This method validates the details entered, sends a message to activate a group guide with the provided ID,
     * and processes the server response accordingly. It updates the error message label based on the response received.
     * If the activation is successful, it displays a success message in green; otherwise, it displays an error message in red.
     *
     * @param ignoredEvent The ActionEvent triggered by clicking the "SubmitButton".
     */
    @FXML
    void OnClickSubmitButton(ActionEvent ignoredEvent) {
        if (!isDetailsValid(getID())) {
            lblErrorMsgID.setText("Invalid ID");
            return;
        }
        lblErrorMsgID.setText("");
        Object msg = new Message(OpCodes.OP_ACTIVATE_GROUP_GUIDE, this.applicationWindowController.getUser().getUsername(), getID());
        ClientUI.client.accept(msg);

        Message response = ClientCommunicator.msg;
        OpCodes returnOpCode = response.getMsgOpcode();
        if (returnOpCode == OpCodes.OP_DB_ERR) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        // Checking if the response from the server is inappropriate.
        if (returnOpCode != OpCodes.OP_ACTIVATE_GROUP_GUIDE) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        if (!(response.getMsgData() instanceof String) && response.getMsgData() != null) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }

        if (response.getMsgData() == null) {
            updateMsg("Group guide has successfully registered!", "#008000");
        } else {
            updateMsg((String) response.getMsgData(), "#FF0000");
        }


    }


    /**
     * Updates the error message displayed in a label with custom styling.
     *
     * @param msg       The message to be displayed.
     * @param colorCode The color code to be applied to the text.
     *                  It should be in the hexadecimal format, e.g., "#FF0000" for red.
     */
    private void updateMsg(String msg, String colorCode) {
        lblErrorMsgGeneral.setText(msg);
        lblErrorMsgGeneral.setStyle("-fx-text-fill: " + colorCode + ";" +
                "-fx-font-size: 14px; " +
                "-fx-padding: 10px 10px; " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 5px; " +
                "-fx-alignment: center;");
    }

}