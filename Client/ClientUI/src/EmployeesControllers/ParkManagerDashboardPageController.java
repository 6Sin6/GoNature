package EmployeesControllers;

import CommonClient.controllers.BaseController;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class ParkManagerDashboardPageController extends BaseController {

    @FXML
    private MFXButton btnAvailableSpot;

    @FXML
    private MFXButton btnChangeParkParameters;

    public void cleanup() {
        // No cleanup required
    }

    @FXML
    public void OnClickChangeParkParametersButton(ActionEvent event)
    {
        applicationWindowController.setCenterPage("/EmployeesUI/RequestSettingParkParametersPage.fxml");
        if (applicationWindowController.getCurrentActiveController() instanceof RequestSettingParkParametersController) {
            ((RequestSettingParkParametersController) applicationWindowController.getCurrentActiveController()).getParkParameters();
        }
    }

    @FXML
    public void OnClickGenerateReportsButton(ActionEvent event)
    {
        applicationWindowController.setCenterPage("/EmployeesUI/PrepareReportsPage.fxml");
    }

}
