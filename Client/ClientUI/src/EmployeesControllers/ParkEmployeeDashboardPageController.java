package EmployeesControllers;

import CommonClient.ClientUI;
import CommonClient.controllers.BaseController;
import CommonUtils.ConfirmationPopup;
import CommonUtils.InputTextPopup;
import javafx.scene.layout.BorderPane;
import CommonUtils.MessagePopup;
import Entities.Message;
import client.ClientCommunicator;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.util.Duration;

import static Entities.OpCodes.OP_UPDATE_EXIT_TIME_OF_ORDER;

public class ParkEmployeeDashboardPageController extends BaseController {

    @FXML
    private MFXButton btnAvailableSpot;

    @FXML
    private MFXButton btnExit;

    @FXML
    private MFXButton btnGenerateBill;

    private InputTextPopup popup;

    private BorderPane borderpane;

    public void cleanup() {
        // No cleanup required
    }

    @FXML
    public void OnClickAvailableSpotButton(ActionEvent event)
    {
        applicationWindowController.loadEmployeesPage("CheckAvailableSpotsPage");
    }

    @FXML
    public void OnClickGenerateBillButton(ActionEvent event)
    {
        applicationWindowController.loadEmployeesPage("GenerateBillPage");
    }


    private void onSubmit(String[] inputs)
    {
        String orderID = inputs[0];
        if (!CommonUtils.CommonUtils.isValidOrderID(orderID))
        {
            popup.setErrorLabel("Invalid Order ID");
            return;
        }
        Object message = new Message(OP_UPDATE_EXIT_TIME_OF_ORDER, null, orderID);
        ClientUI.client.accept(message);
        Object answer = ClientCommunicator.msg.getMsgData();
        if (answer != null)
            popup.setErrorLabel(answer.toString());
        else {
            popup.setLabelColor("#00FF00");
            popup.setErrorLabel("Order Exited Successfully!");
        }

    }
    @FXML
    void OnClickExitButton(ActionEvent event)
    {
        popup = new InputTextPopup(new String[]{"Enter Order ID"}, this::onSubmit, 0, 0, true, true, false);
        popup.show(applicationWindowController.getRoot());
    }

}
