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

    @FXML
    private Label lblErrorMsgGeneral;

    @FXML
    private Label lblErrorMsgID;

    @FXML
    private TextField txtID;

    public void cleanup() {
        txtID.setText("");
        lblErrorMsgGeneral.setText("");
        lblErrorMsgID.setText("");
    }

    private boolean isDetailsValid(String id) {
        return (CommonUtils.isValidID(id));
    }

    private String getID() {
        return txtID.getText();
    }

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
        if (!(response.getMsgData() instanceof String)) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }

        String msgAnswer = (String) response.getMsgData();

        if (msgAnswer == null)
            updateMsg("Group guide has successfully registered!", "#008000");
        else
            updateMsg(msgAnswer, "#FF0000");
    }

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