package EmployeesControllers;

import CommonClient.controllers.BaseController;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.text.Text;

public class DepartmentManagerDashboardPageController extends BaseController {

    @FXML
    private MFXButton btnIssueReports;

    @FXML
    private MFXButton btnViewRequests;

    @FXML
    private Text errorTxt;

    @FXML
    public void OnClickIssueReportsButton(ActionEvent event)
    {
        applicationWindowController.setCenterPage("/EmployeesUI/IssueReportsPage.fxml");
    }

    @FXML
    public void OnClickViewRequestsButton(ActionEvent event) {
        try {
            applicationWindowController.setCenterPage("/EmployeesUI/AuthorizeParksRequestsPage.fxml");
            if (applicationWindowController.currentActiveController instanceof AuthorizeParksRequestsController) {
                AuthorizeParksRequestsController controller = (AuthorizeParksRequestsController) applicationWindowController.currentActiveController;
                controller.start();
            }
        } catch (Exception e) {
            errorTxt.setText("Something went wrong... Try again later.");
        }
    }
}
