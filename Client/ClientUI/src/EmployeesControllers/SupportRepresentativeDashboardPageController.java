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

public class SupportRepresentativeDashboardPageController extends GeneralEmployeeDashboard {

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

    @FXML
    public void OnClickRegisterGuideButton(ActionEvent event)
    {
        applicationWindowController.loadEmployeesPage("RegisterGroupGuidePage");
    }

    @FXML
    void OnClickExitButton(ActionEvent event)
    {
        popup = new InputTextPopup(new String[]{"Enter Order ID"}, this::onSubmit, 0, 0, true, true, false);
        popup.show(applicationWindowController.getRoot());
    }
}
