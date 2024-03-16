package EmployeesControllers;

import CommonClient.controllers.BaseController;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class PrepareReportsController extends BaseController {

    @FXML
    private MFXButton btnUsageReport;

    @FXML
    private MFXButton btnVisitorsReport;

    public void cleanup() {

    }

    @FXML
    void OnClickUsageReportButton(ActionEvent event)
    {
        applicationWindowController.setCenterPage("/EmployeesUI/PrepareUsageReportPage.fxml");
    }

    @FXML
    void OnClickVisitorsReportButton(ActionEvent event)
    {
        applicationWindowController.setCenterPage("/EmployeesUI/PrepareNumberOfVisitorsReportPage.fxml");
    }
}
