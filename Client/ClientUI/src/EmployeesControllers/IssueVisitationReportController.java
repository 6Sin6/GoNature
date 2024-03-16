package EmployeesControllers;

import CommonClient.controllers.BaseController;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.text.Text;

public class IssueVisitationReportController extends BaseController {

    @FXML
    private MFXButton btnDownloadCSV;

    @FXML
    private StackedBarChart<?, ?> chartVisitationStatistics;

    @FXML
    private Text txtDate;

    @FXML
    private Text txtNoReport;

    public void cleanup() {
        txtDate.setText("");
        txtNoReport.setVisible(false);
        chartVisitationStatistics.getData().clear();
    }

    @FXML
    void OnClickDownloadCSVButton(ActionEvent event) {

    }

}
