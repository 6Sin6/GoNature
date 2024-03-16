package EmployeesControllers;

import CommonClient.controllers.BaseController;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class SupportRepresentativeDashboardPageController extends BaseController {

    @FXML
    private MFXButton btnCheckParkAvailability;

    @FXML
    private MFXButton btnIssueBill;

    @FXML
    private MFXButton btnRegisterGuide;

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
}
