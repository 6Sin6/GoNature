package EmployeesControllers;

import CommonClient.controllers.BaseController;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class DepartmentManagerDashboardPageController extends BaseController {

    @FXML
    private MFXButton btnIssueReports;

    @FXML
    private MFXButton btnViewRequests;

    @FXML
    public void OnClickIssueReportsButton(ActionEvent event)
    {
        applicationWindowController.setCenterPage("/EmployeesUI/IssueReportsPage.fxml");
    }

    @FXML
    public void OnClickViewRequestsButton(ActionEvent event)
    {
        applicationWindowController.setCenterPage("/EmployeesUI/AuthorizeParksRequestsPage.fxml");
    }

}
