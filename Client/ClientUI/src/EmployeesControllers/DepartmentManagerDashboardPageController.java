package EmployeesControllers;

import CommonClient.ClientUI;
import CommonClient.controllers.BaseController;
import CommonUtils.CommonUtils;
import CommonUtils.ConfirmationPopup;
import Entities.Message;
import Entities.OpCodes;
import Entities.ParkDepartmentManager;
import client.ClientCommunicator;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class DepartmentManagerDashboardPageController extends BaseController {
    @FXML
    private MFXButton btnIssueReports;

    @FXML
    private MFXButton btnViewReports;

    @FXML
    private MFXButton btnViewRequests;

    @FXML
    private Text errorTxt;

    @FXML
    private Text depMgrParks;

    @FXML
    private Text depMgrWelcome;

    public void cleanup() {
        depMgrParks.setText("");
        depMgrWelcome.setText("");
        errorTxt.setText("");
    }

    @FXML
    public void OnClickIssueReportsButton(ActionEvent event) {
        applicationWindowController.loadEmployeesPage("IssueReportsPage");
        if (applicationWindowController.getCurrentActiveController() instanceof IssueReportsController) {
            ((IssueReportsController) applicationWindowController.getCurrentActiveController()).start();
        }
    }

    @FXML
    public void OnClickViewReportsButton(ActionEvent event) {
        applicationWindowController.loadEmployeesPage("ViewReportsPage");
        if (applicationWindowController.getCurrentActiveController() instanceof ViewReportsPageController) {
            ((ViewReportsPageController) applicationWindowController.getCurrentActiveController()).start();
        }
    }

    @FXML
    public void OnClickViewRequestsButton(ActionEvent event) {
        try {
            applicationWindowController.loadEmployeesPage("AuthorizeParksRequestsPage");
            if (applicationWindowController.getCurrentActiveController() instanceof AuthorizeParksRequestsController) {
                AuthorizeParksRequestsController controller = (AuthorizeParksRequestsController) applicationWindowController.getCurrentActiveController();
                controller.start();
            }
        } catch (Exception e) {
            errorTxt.setText("Something went wrong... Try again later.");
        }
    }
}
