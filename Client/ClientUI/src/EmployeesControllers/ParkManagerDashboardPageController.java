package EmployeesControllers;

import CommonClient.controllers.BaseController;
import Entities.ParkBank;
import Entities.ParkManager;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class ParkManagerDashboardPageController extends BaseController {

    @FXML
    private MFXButton btnChangeParkParameters;

    @FXML
    private MFXButton btnIssueReports;

    @FXML
    private MFXButton btnViewReports;

    @FXML
    private Text parkMgrTxt;


    public void cleanup() {
        parkMgrTxt.setText("");
    }

    public void start() {
        parkMgrTxt.setText("Welcome, " + applicationWindowController.getUser().getUsername() + "! Your park: " + (ParkBank.getParkNameByID(((ParkManager) applicationWindowController.getUser()).getParkID())));
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
}
