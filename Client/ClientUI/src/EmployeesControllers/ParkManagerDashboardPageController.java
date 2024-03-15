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

    @FXML
    public void OnClickChangeParkParametersButton(ActionEvent event)
    {
        applicationWindowController.setCenterPage("/EmployeesUI/RequestSettingParkParametersPage.fxml");
        if (applicationWindowController.currentActiveController instanceof RequestSettingParkParametersController) {
            ((RequestSettingParkParametersController) applicationWindowController.currentActiveController).getParkParameters();
        }
    }

    @FXML
    public void OnClickGenerateReportsButton(ActionEvent event)
    {
        applicationWindowController.setCenterPage("/EmployeesUI/PrepareReportsPage.fxml");
    }

}
