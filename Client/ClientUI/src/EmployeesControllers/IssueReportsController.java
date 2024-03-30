package EmployeesControllers;

import CommonClient.ClientUI;
import CommonClient.Utils;
import CommonClient.controllers.BaseController;
import Entities.Message;
import Entities.OpCodes;
import Entities.ParkDepartmentManager;
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
    /**
     * The button that triggers the generation of a report.
     */
    @FXML
    private MFXButton btnGenerateReport;

    /**
     * A label that displays any error messages.
     */
    @FXML
    private Label errMsg;

    /**
     * A label that displays the result of the report generation process.
     */
    @FXML
    private Label generateResultMsg;

    /**
     * A combo box that contains the available reports.
     */
    @FXML
    private ComboBox<String> reportCmb;

    /**
     * An image view that is displayed while the report is being generated.
     */
    @FXML
    private ImageView imgLoading;

    /**
     * A boolean indicating whether the page is displaying park manager reports or department reports.
     */
    private boolean parkManagerPage = false;


    /**
     * Cleans up the UI elements of the IssueReportsController.
     */
    public void cleanup() {
        errMsg.setText("");
        generateResultMsg.setText("");
        reportCmb.getSelectionModel().clearSelection();
        reportCmb.getItems().clear();
    }


    /**
     * Checks if the currently connected user is a Department Manager.
     *
     * @return true if the user is a Department Manager, false otherwise
     */
    private boolean isDepartmentManager() {
        return (applicationWindowController.getUser() instanceof ParkDepartmentManager);
    }


    /**
     * Initializes the report combo box and sets up its event listeners based on the user's role.
     * If the user is a department manager, department reports are loaded into the combo box.
     * If the user is a park manager, park manager reports are loaded into the combo box.
     * Also sets the default selected report and adds a listener to handle report selection changes.
     */
    public void start() {
        if (this.isDepartmentManager()) // Department manager connected
        {
            reportCmb.getItems().addAll(Utils.departmentReportsMap.keySet());
        } else // Park manager connected
        {
            reportCmb.getItems().addAll(Utils.parkManagerReportsMap.keySet());
            parkManagerPage = true;
        }

        reportCmb.setValue(reportCmb.getItems().get(0));
        reportCmb.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> onSelectReport());
    }


    /**
     * This method is called when the user selects a report from the combo box.
     * It sets the generateResultMsg label to an empty string and clears the
     * selected item in the combo box.
     */
    private void onSelectReport() {
        generateResultMsg.setText("");
    }


    /**
     * Handles the action event triggered when the "Generate Report" button is clicked.
     * Initiates the process of generating a report based on the selected report type from the combo box.
     * Displays relevant UI elements during the report generation process and updates them accordingly upon completion.
     */
    @FXML
    void onClickGenerateReport(ActionEvent ignoredEvent) {
        btnGenerateReport.setDisable(true);
        generateResultMsg.setText("Generating report...");
        imgLoading.setVisible(true);
        applicationWindowController.toggleMenuButtons(true, true, true, false, true, true);

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

        generateReportTask.setOnSucceeded(event -> Platform.runLater(() -> {
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

        generateReportTask.setOnFailed(event -> Platform.runLater(() -> {
            generateResultMsg.setText("");
            errMsg.setText("Failed to generate report");
            applicationWindowController.toggleMenuButtons(false);
        }));

        generateReportTask.setOnCancelled(event -> Platform.runLater(() -> {
            generateResultMsg.setText("");
            errMsg.setText("Failed to generate report");
            applicationWindowController.toggleMenuButtons(false);
        }));

        new Thread(generateReportTask).start();
    }
}
