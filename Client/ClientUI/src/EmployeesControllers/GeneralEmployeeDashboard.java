package EmployeesControllers;

import CommonClient.ClientUI;
import CommonClient.controllers.BaseController;
import CommonUtils.InputTextPopup;
import Entities.Message;
import client.ClientCommunicator;

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
        if (!CommonUtils.CommonUtils.isValidOrderID(orderID))
        {
            popup.setErrorLabel("Invalid Order ID");
            return;
        }
        Object message = new Message(OP_UPDATE_EXIT_TIME_OF_ORDER, null, orderID);
        ClientUI.client.accept(message);
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
}
