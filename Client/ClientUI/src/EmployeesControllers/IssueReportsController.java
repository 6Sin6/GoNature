package EmployeesControllers;

import CommonClient.controllers.BaseController;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class IssueReportsController extends BaseController {

    @FXML
    private MFXButton btnCancellationReports;

    @FXML
    private MFXButton btnVisitationsReports;

    @FXML
    void OnClickCancellationReportsButton(ActionEvent event)
    {
        applicationWindowController.setCenterPage("/EmployeesUI/IssueCancellationReportPage.fxml");
    }

    @FXML
    void OnClickVisitationsReportsButton(ActionEvent event)
    {
        applicationWindowController.setCenterPage("/EmployeesUI/IssueVisitationReportPage.fxml");
    }

}
