package EmployeesControllers;

import CommonClient.controllers.BaseController;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class IssueCancellationReportController extends BaseController {
    @FXML
    private MFXButton btnGenerateReport;

    @FXML
    private MFXButton btnViewReport;

    @FXML
    private Label errMsg;

    @FXML
    private Label generateResultMsg;

    @FXML
    private MFXComboBox<?> monthCmb;

    @FXML
    private Text txtNoReport;

    @FXML
    private MFXComboBox<?> yearCmb;

    public void cleanup() {
        txtNoReport.setVisible(false);
        generateResultMsg.setText("");
        monthCmb.getSelectionModel().clearSelection();
        yearCmb.getSelectionModel().clearSelection();
    }

}
