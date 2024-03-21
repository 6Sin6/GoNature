package EmployeesControllers;

import CommonClient.ClientUI;
import CommonClient.controllers.BaseController;
import CommonUtils.InputTextPopup;
import Entities.Message;
import client.ClientCommunicator;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import static Entities.OpCodes.OP_UPDATE_EXIT_TIME_OF_ORDER;

public class SupportRepresentativeDashboardPageController extends BaseController {

    @FXML
    private MFXButton btnCheckParkAvailability;

    @FXML
    private MFXButton btnEntry;

    @FXML
    private MFXButton btnIssueBill;

    @FXML
    private MFXButton btnRegisterGuide;

    private InputTextPopup popup;

    public void cleanup() {
        // No cleanup required
    }

    @FXML
    public void OnClickCheckParkAvailabilityButton(ActionEvent event)
    {
        applicationWindowController.loadEmployeesPage("CheckAvailableSpotsPage");
    }

    @FXML
    public void OnClickIssueBillButton(ActionEvent event)
    {
        applicationWindowController.loadEmployeesPage("GenerateBillPage");
    }

    @FXML
    public void OnClickRegisterGuideButton(ActionEvent event)
    {
        applicationWindowController.loadEmployeesPage("RegisterGroupGuidePage");
    }

    private void onSubmit(String[] inputs) {
        String orderID = inputs[0];
        if (!CommonUtils.CommonUtils.isValidOrderID(orderID)) {
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
        else
        {
            popup.setLabelColor("#008000");
            popup.setErrorLabel("Order has exited successfully!");
        }
    }

    @FXML
    void OnClickExitButton(ActionEvent event)
    {
        popup = new InputTextPopup(new String[]{"Enter Order ID"}, this::onSubmit, 0, 0, true, true, false);
        popup.show(applicationWindowController.getRoot());
    }
}
