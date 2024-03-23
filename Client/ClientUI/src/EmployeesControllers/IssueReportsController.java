package EmployeesControllers;

import CommonClient.ClientUI;
import CommonClient.Utils;
import CommonClient.controllers.BaseController;
import Entities.*;
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

    @FXML
    private ComboBox<String> parkCmb;

    private boolean parkManagerPage = false;

    public void cleanup() {
        errMsg.setText("");
        generateResultMsg.setText("");
        reportCmb.getSelectionModel().clearSelection();
        reportCmb.getItems().clear();
        parkCmb.getSelectionModel().clearSelection();
        parkCmb.getItems().clear();
    }


    public void start() {
        if (applicationWindowController.getUser() instanceof ParkDepartmentManager) {
            reportCmb.getItems().addAll(Utils.departmentReportsMap.keySet());
            parkCmb.setValue("All Parks");
            parkCmb.setDisable(true);
        } else {
            parkCmb.setValue("");
            parkManagerPage = true;
        }
        reportCmb.getItems().addAll(Utils.parkManagerReportsMap.keySet());
        parkCmb.getItems().addAll(ParkBank.getUnmodifiableMap().keySet());
        reportCmb.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            onSelectReport();
        });
    }

    void onSelectReport() {
        String selectedReport = reportCmb.getValue();
        if (selectedReport == null) {
            return;
        }

        if (Utils.parkManagerReportsMap.containsKey(selectedReport)) {
            parkCmb.setDisable(false);
        } else {
            parkCmb.setDisable(true);
            parkCmb.setValue("All Parks");
        }
    }

    @FXML
    void onClickGenerateReport(ActionEvent event) {
        String selectedReport = reportCmb.getValue();
        if (selectedReport == null) {
            errMsg.setText("Please select a report type");
            return;
        }

        String selectedPark = parkCmb.getValue();
        if (Utils.parkManagerReportsMap.containsKey(selectedReport) && selectedPark == null) {
            errMsg.setText("This is a park specific report. Please select a park");
            return;
        } else if (Utils.departmentReportsMap.containsKey(selectedReport)) {
            parkCmb.setValue("All Parks");
        }
        errMsg.setText("");


        String reportType = parkManagerPage ?
                Utils.parkManagerReportsMap.get(selectedReport) :
                    Utils.departmentReportsMap.get(selectedReport) != null ? Utils.departmentReportsMap.get(selectedReport) :
                            Utils.parkManagerReportsMap.get(selectedReport);

        Message msg = new Message(
                OpCodes.OP_GENERATE_REPORT_BLOB,
                applicationWindowController.getUser().getUsername(),
                reportType
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
