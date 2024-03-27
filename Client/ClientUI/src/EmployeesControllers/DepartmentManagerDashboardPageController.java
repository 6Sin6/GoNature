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

    public void start() {
        depMgrWelcome.setText("Welcome, " + applicationWindowController.getUser().getUsername() + "! The parks under your management are:");
        Message msg = new Message(OpCodes.OP_GET_DEPARTMENT_MANAGER_PARKS, applicationWindowController.getUser().getUsername(), ((ParkDepartmentManager) applicationWindowController.getUser()).getDepartmentID());
        ClientUI.client.accept(msg);
        Message response = ClientCommunicator.msg;
        if (response.getMsgOpcode() == OpCodes.OP_DB_ERR) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }

        if (response.getMsgOpcode() != OpCodes.OP_GET_DEPARTMENT_MANAGER_PARKS) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }

        ArrayList<String> parks = (ArrayList<String>) response.getMsgData();
        StringBuilder parkNames = new StringBuilder();
        for (String park : parks) {
            parkNames.append(park);
            if (parks.indexOf(park) != parks.size() - 1) {
                parkNames.append(", ");
            }
        }
        depMgrParks.setText(parkNames.toString());
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
