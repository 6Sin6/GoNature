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

    @FXML
    public void OnClickCheckParkAvailabilityButton(ActionEvent event)
    {
        applicationWindowController.setCenterPage("/EmployeesUI/CheckAvailableSpotsPage.fxml");
    }

    @FXML
    public void OnClickIssueBillButton(ActionEvent event)
    {
        applicationWindowController.setCenterPage("/EmployeesUI/GenerateBillPage.fxml");
    }

    @FXML
    public void OnClickRegisterGuideButton(ActionEvent event)
    {
        applicationWindowController.setCenterPage("/EmployeesUI/RegisterGroupGuidePage.fxml");
    }
}
