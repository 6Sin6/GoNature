package EmployeesControllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.text.Text;

public class IssueVisitationReportController {

    @FXML
    private MFXButton btnDownloadCSV;

    @FXML
    private StackedBarChart<?, ?> chartVisitationStatistics;

    @FXML
    private Text txtDate;

    @FXML
    private Text txtNoReport;

    @FXML
    void OnClickDownloadCSVButton(ActionEvent event) {

    }

}
