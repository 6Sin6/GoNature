package EmployeesControllers;

import CommonClient.ClientUI;
import CommonClient.controllers.BaseController;
import CommonUtils.ConfirmationPopup;
import CommonUtils.*;
import CommonUtils.InputTextPopup;
import Entities.Message;
import Entities.OpCodes;
import client.ClientCommunicator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import static Entities.OpCodes.OP_UPDATE_EXIT_TIME_OF_ORDER;

public abstract class GeneralEmployeeDashboard extends BaseController
{
    protected InputTextPopup popup;

    public void cleanup()
    {
        // No cleanup required
    }

    protected void onSubmit(String[] inputs)
    {
        String orderID = inputs[0];
        if (!CommonUtils.isValidOrderID(orderID))
        {
            popup.setErrorLabel("Invalid Order ID");
            return;
        }
        Object message = new Message(OP_UPDATE_EXIT_TIME_OF_ORDER, null, orderID);
        ClientUI.client.accept(message);
        Message response = ClientCommunicator.msg;
        OpCodes returnOpCode = response.getMsgOpcode();
        if(returnOpCode == OpCodes.OP_DB_ERR)
        {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        // Checking if the response from the server is inappropriate.
        if (returnOpCode != OpCodes.OP_UPDATE_EXIT_TIME_OF_ORDER) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        if(!(response.getMsgData() instanceof String)) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }

        String answer = ClientCommunicator.msg.getMsgData().toString();
        if (answer != null)
        {
            popup.setLabelColor("#FF0000");
            popup.setErrorLabel(answer);
        }
        else {
            popup.setLabelColor("#00FF00");
            popup.setErrorLabel("Order Exited Successfully!");
        }
    }

    @FXML
    public void OnClickAvailableSpotButton(ActionEvent ignoredEvent)
    {
        applicationWindowController.loadEmployeesPage("CheckAvailableSpotsPage");
    }

    @FXML
    public void OnClickGenerateBillButton(ActionEvent ignoredEvent)
    {
        applicationWindowController.loadEmployeesPage("GenerateBillPage");
    }


    @FXML
    void OnClickExitButton(ActionEvent ignoredEvent)
    {
        popup = new InputTextPopup(new String[]{"Enter Order ID"}, this::onSubmit, 0, 0, true, true, false);
        popup.show(applicationWindowController.getRoot());
    }
}
