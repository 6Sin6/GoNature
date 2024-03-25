package EmployeesControllers;

import CommonClient.ClientUI;
import CommonClient.Utils;
import CommonClient.controllers.BaseController;
import Entities.*;
import client.ClientCommunicator;
import io.github.palexdev.materialfx.controls.MFXButton;
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
    private ComboBox<String> parkCmb;

    @FXML
    private ImageView imgLoading;

    private boolean parkManagerPage = false;

    public void cleanup()
    {
        errMsg.setText("");
        generateResultMsg.setText("");
        reportCmb.getSelectionModel().clearSelection();
        reportCmb.getItems().clear();
        parkCmb.getSelectionModel().clearSelection();
        parkCmb.getItems().clear();
    }

    private boolean isDepartmentManager()
    {
        return (applicationWindowController.getUser() instanceof ParkDepartmentManager);
    }

    private String getParkName_ParkManager()
    {
        String parkID =  ((ParkManager)applicationWindowController.getUser()).getParkID();
        Message msg = new Message(OpCodes.OP_GET_PARK_NAME_BY_PARK_ID, applicationWindowController.getUser().getUsername(), parkID);
        ClientUI.client.accept(msg);

        Message response = ClientCommunicator.msg;
        if (response.getMsgData() == null)
            return "";
        return (String) response.getMsgData();
    }

    public void start()
    {
        if (this.isDepartmentManager()) // Department manager connected
        {
            reportCmb.getItems().addAll(Utils.departmentReportsMap.keySet());
            parkCmb.getItems().addAll(ParkBank.getUnmodifiableMap().keySet());
            parkCmb.setValue("All Parks");
            parkCmb.setDisable(true);
        }
        else // Park manager connected
        {
            parkCmb.setValue(this.getParkName_ParkManager());
            parkManagerPage = true;
            parkCmb.setDisable(true);
        }

        reportCmb.getItems().addAll(Utils.parkManagerReportsMap.keySet());
        reportCmb.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            onSelectReport();
        });

        reportCmb.setValue(reportCmb.getItems().get(0));
    }

    void onSelectReport()
    {
        if (!isDepartmentManager()) // if a park manager is connected, parksCmb stays disabled.
            return;

        String selectedReport = reportCmb.getValue();

        if (Utils.parkManagerReportsMap.containsKey(selectedReport))
        {
            parkCmb.setDisable(false);
            parkCmb.setValue(parkCmb.getItems().get(0));
        } else
        {
            parkCmb.setDisable(true);
            parkCmb.setValue("All Parks");
        }
    }

    @FXML
    void onClickGenerateReport(ActionEvent ignoredEvent)
    {
        btnGenerateReport.setDisable(true);
        generateResultMsg.setText("Generating report...");
        imgLoading.setVisible(true);

        String selectedReport = reportCmb.getValue();
        String reportType;

        if (!this.parkManagerPage && Utils.departmentReportsMap.get(selectedReport) != null)
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
        new Thread(generateReportTask).start();
    }
}
