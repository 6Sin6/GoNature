package EmployeesControllers;

import CommonClient.controllers.BaseController;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class ParkManagerDashboardPageController extends BaseController {

    @FXML
    private MFXButton btnChangeParkParameters;

    @FXML
    private MFXButton btnIssueReports;

    @FXML
    private MFXButton btnViewReports;

    public void cleanup() {
        // No cleanup required
    }

    @FXML
    public void OnClickChangeParkParametersButton(ActionEvent event)
    {
        applicationWindowController.loadEmployeesPage("RequestSettingParkParametersPage");
        if (applicationWindowController.getCurrentActiveController() instanceof RequestSettingParkParametersController) {
            ((RequestSettingParkParametersController) applicationWindowController.getCurrentActiveController()).getParkParameters();
        }
    }

    @FXML
    public void OnClickIssueReportsButton(ActionEvent event)
    {
        applicationWindowController.loadEmployeesPage("IssueReportsPage");
        if (applicationWindowController.getCurrentActiveController() instanceof IssueReportsController) {
            ((IssueReportsController) applicationWindowController.getCurrentActiveController()).start();
        }
    }
    @FXML
    public void OnClickViewReportsButton(ActionEvent event)
    {
        applicationWindowController.loadEmployeesPage("ViewReportsPage");
        if (applicationWindowController.getCurrentActiveController() instanceof ViewReportsPageController) {
            ((ViewReportsPageController) applicationWindowController.getCurrentActiveController()).start();
        }
    }

    @FXML
    public void OnClickGenerateReportsButton(ActionEvent event)
    {
        applicationWindowController.loadEmployeesPage("PrepareReportsPage");
    }

}
