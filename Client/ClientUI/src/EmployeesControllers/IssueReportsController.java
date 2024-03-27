package EmployeesControllers;

import CommonClient.ClientUI;
import CommonClient.Utils;
import CommonClient.controllers.BaseController;
import Entities.*;
import client.ClientCommunicator;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;


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
    private ImageView imgLoading;

    private boolean parkManagerPage = false;

    public void cleanup()
    {
        errMsg.setText("");
        generateResultMsg.setText("");
        reportCmb.getSelectionModel().clearSelection();
        reportCmb.getItems().clear();
    }

    private boolean isDepartmentManager()
    {
        return (applicationWindowController.getUser() instanceof ParkDepartmentManager);
    }

    public void start()
    {
        if (this.isDepartmentManager()) // Department manager connected
        {
            reportCmb.getItems().addAll(Utils.departmentReportsMap.keySet());
        }
        else // Park manager connected
        {
            reportCmb.getItems().addAll(Utils.parkManagerReportsMap.keySet());
            parkManagerPage = true;
        }

        reportCmb.setValue(reportCmb.getItems().get(0));
        reportCmb.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> onSelectReport());
    }

    private void onSelectReport()
    {
        generateResultMsg.setText("");
    }





    @FXML
    void onClickGenerateReport(ActionEvent ignoredEvent)
    {
        btnGenerateReport.setDisable(true);
        generateResultMsg.setText("Generating report...");
        imgLoading.setVisible(true);
        applicationWindowController.toggleMenuButtons(true);

        String selectedReport = reportCmb.getValue();
        String reportType;

        if (!this.parkManagerPage)
            reportType = Utils.departmentReportsMap.get(selectedReport);
        else
            reportType = Utils.parkManagerReportsMap.get(selectedReport);

        Task<Void> generateReportTask = new Task<Void>() {
            @Override
            protected Void call() {
                Message msg = new Message(
                        OpCodes.OP_GENERATE_REPORT_BLOB,
                        applicationWindowController.getUser().getUsername(),
                        reportType
                );

                ClientUI.client.accept(msg);
                return null;
            }
        };

        generateReportTask.setOnSucceeded(event -> Platform.runLater(() -> { // Update UI on JavaFX Application Thread
            btnGenerateReport.setDisable(false);
            applicationWindowController.toggleMenuButtons(false);
            imgLoading.setVisible(false);

            Message response = ClientCommunicator.msg;
            if (response.getMsgOpcode() == OpCodes.OP_GENERATE_REPORT_BLOB) {
                if ((Boolean) response.getMsgData()) {
                    errMsg.setText("");
                    generateResultMsg.setText("Report generated successfully");
                } else {
                    generateResultMsg.setText("");
                    errMsg.setText("Failed to generate report");
                }
            }
        }));

        generateReportTask.setOnFailed(event -> Platform.runLater(() -> { // Update UI on JavaFX Application Thread
            generateResultMsg.setText("");
            errMsg.setText("Failed to generate report");
            applicationWindowController.toggleMenuButtons(false);
        }));

        generateReportTask.setOnCancelled(event -> Platform.runLater(() -> { // Update UI on JavaFX Application Thread
            generateResultMsg.setText("");
            errMsg.setText("Failed to generate report");
            applicationWindowController.toggleMenuButtons(false);
        }));

        new Thread(generateReportTask).start();
    }


/*
    @FXML
    void onClickGenerateReport(ActionEvent ignoredEvent)
    {
        btnGenerateReport.setDisable(true);
        generateResultMsg.setText("Generating report...");
        imgLoading.setVisible(true);
        applicationWindowController.toggleMenuButtons(true);

        String selectedReport = reportCmb.getValue();
        String reportType;

        if (!this.parkManagerPage)
            reportType = Utils.departmentReportsMap.get(selectedReport);
        else reportType = Utils.parkManagerReportsMap.get(selectedReport);

        Task<Void> generateReportTask = new Task<Void>() {
            @Override
            protected Void call() {
                Message msg = new Message(
                        OpCodes.OP_GENERATE_REPORT_BLOB,
                        applicationWindowController.getUser().getUsername(),
                        reportType
                );

                ClientUI.client.accept(msg);
                return null;
            }
        };

        generateReportTask.setOnSucceeded(event -> {
            btnGenerateReport.setDisable(false);
            applicationWindowController.toggleMenuButtons(false);
            imgLoading.setVisible(false);

            Message response = ClientCommunicator.msg;
            if (response.getMsgOpcode() == OpCodes.OP_GENERATE_REPORT_BLOB)
            {
                if ((Boolean) response.getMsgData())
                {
                    errMsg.setText("");
                    generateResultMsg.setText("Report generated successfully");
                } else
                {
                    generateResultMsg.setText("");
                    errMsg.setText("Failed to generate report");
                }
            }
        });

        generateReportTask.setOnFailed(event ->
        {
            generateResultMsg.setText("");
            errMsg.setText("Failed to generate report");
            applicationWindowController.toggleMenuButtons(false);
        });

        generateReportTask.setOnCancelled(event ->
        {
            generateResultMsg.setText("");
            errMsg.setText("Failed to generate report");
            applicationWindowController.toggleMenuButtons(false);
        });

    }

 */
}
