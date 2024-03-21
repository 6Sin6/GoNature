package EmployeesControllers;

import CommonClient.ClientUI;
import CommonClient.Utils;
import CommonClient.controllers.BaseController;
import Entities.Message;
import Entities.OpCodes;
import Entities.ParkManager;
import client.ClientCommunicator;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class IssueReportsController extends BaseController {
    @FXML
    private MFXButton btnGenerateReport;

    @FXML
    private Label errMsg;

    @FXML
    private Label generateResultMsg;

    @FXML
    private ComboBox<String> reportCmb;

    private boolean parkManagerPage = false;

    public void cleanup() {
        errMsg.setText("");
        generateResultMsg.setText("");
        reportCmb.getSelectionModel().clearSelection();
    }


    public void start() {
        if (applicationWindowController.getUser() instanceof ParkManager) {
            parkManagerPage = true;
            reportCmb.getItems().addAll(Utils.parkManagerReportsMap.keySet());
            return;
        }
        reportCmb.getItems().addAll(Utils.departmentReportsMap.keySet());
    }

    @FXML
    void onClickGenerateReport(ActionEvent event) {
        String selectedReport = reportCmb.getValue();
        if (selectedReport == null) {
            errMsg.setText("Please select a report type");
            return;
        }
        errMsg.setText("");


        Message msg = new Message(
                OpCodes.OP_GENERATE_REPORT_BLOB,
                applicationWindowController.getUser().getUsername(),
                parkManagerPage ? Utils.parkManagerReportsMap.get(selectedReport) : Utils.departmentReportsMap.get(selectedReport)
        );

        ClientUI.client.accept(msg);

        Message response = ClientCommunicator.msg;
        if (response.getMsgOpcode() == OpCodes.OP_GENERATE_REPORT_BLOB) {
            if ((Boolean) response.getMsgData()) {
                generateResultMsg.setText("Report generated successfully");
            } else {
                generateResultMsg.setText("Failed to generate report");
            }
        }
    }
}
