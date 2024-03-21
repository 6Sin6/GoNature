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

import javax.naming.CommunicationException;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

public class ViewReportsPageController extends BaseController {
    @FXML
    private MFXButton btnViewReport;

    @FXML
    private Label errMsg;

    @FXML
    private ComboBox<String> monthCmb;

    @FXML
    private ComboBox<String> parkCmb;

    @FXML
    private ComboBox<String> reportCmb;

    @FXML
    private Label viewResultMsg;

    @FXML
    private ComboBox<String> yearCmb;

    private boolean parkManagerPage = false;

    public void cleanup() {
        errMsg.setText("");
        viewResultMsg.setText("");
        parkCmb.getSelectionModel().clearSelection();
        reportCmb.getSelectionModel().clearSelection();
        monthCmb.getSelectionModel().clearSelection();
        yearCmb.getSelectionModel().clearSelection();
    }

    public void start() {
        // Populate the ComboBoxes
        monthCmb.getItems().addAll("January", "February", "March", "April", "May", "June", "July", "August", "September",
                "October", "November", "December");

        String[] years = new String[4];
        int currentYear = java.time.Year.now().getValue();
        for (int i = 0; i < 4; i++) {
            years[i] = String.valueOf(currentYear - i);
        }
        yearCmb.getItems().addAll(years);

        Set<String> parkNames = ParkBank.getUnmodifiableMap().keySet();
        for (String park : parkNames) {
            parkCmb.getItems().add(park);
        }

        if (applicationWindowController.getUser() instanceof ParkManager) {
            parkManagerPage = true;
            parkCmb.setValue(ParkBank.getParkNameByID(((ParkManager) applicationWindowController.getUser()).getParkID()));
            parkCmb.setDisable(true);
        } else {
            reportCmb.getItems().addAll(Utils.departmentReportsMap.keySet());
        }
        reportCmb.getItems().addAll(Utils.parkManagerReportsMap.keySet());
    }

    @FXML
    void onClickViewReport(ActionEvent event) throws IOException, CommunicationException {
        if (monthCmb.getValue() == null || yearCmb.getValue() == null || reportCmb.getValue() == null) {
            errMsg.setText("Please select a month, year, park and a report type.");
            return;
        }

        String selectedReport = reportCmb.getValue();
        boolean isDepartmentReport = Arrays.asList(Utils.departmentReports).contains(selectedReport);

        if (!isDepartmentReport && (parkCmb.getValue() == null || parkCmb.getValue().equals("All Parks"))) {
            parkCmb.setValue(null);
            errMsg.setText("This is a park specific report. Please select a park.");
            return;
        }

        // Load PDF Blob from DB.
        String selectedMonth = monthCmb.getValue();
        String selectedYear = yearCmb.getValue();
        if (isDepartmentReport) {
            parkCmb.setValue("All Parks");
        }

        ParkDepartmentManager depMgr = (ParkDepartmentManager) applicationWindowController.getUser();
        String[] params = new String[]{
                String.valueOf(isDepartmentReport),
                isDepartmentReport ? Utils.parkManagerReportsMap.get(selectedReport) : Utils.departmentReportsMap.get(selectedReport),
                selectedMonth,
                selectedYear,
                isDepartmentReport ? String.valueOf(depMgr.getDepartmentID()) : ParkBank.getUnmodifiableMap().get(parkCmb.getValue())
        };
        Message msg = new Message(OpCodes.OP_VIEW_REPORT_BLOB, applicationWindowController.getUser().getUsername(), params);
        ClientUI.client.accept(msg);

        Message response = ClientCommunicator.msg;
        if (response.getMsgOpcode() != OpCodes.OP_VIEW_REPORT_BLOB) {
            viewResultMsg.setText("Something went wrong... Please try again later");
            throw new CommunicationException("Failed to load report.");
        }

        byte[] pdfBlob = (byte[]) response.getMsgData();
        errMsg.setText("");
        if (pdfBlob == null) {
            viewResultMsg.setText("No " + selectedReport + " report found for the selected month and year.");
            return;
        }

        String reportName = selectedReport + " Report"+"_"+selectedYear+"_"+selectedMonth+"_"+parkCmb.getValue();
        File tmpFile = File.createTempFile(reportName, ".pdf");
        tmpFile.deleteOnExit();

        // Write the PDF content to the temp file
        try (FileOutputStream fos = new FileOutputStream(tmpFile)) {
            fos.write(pdfBlob);
        }

        Desktop.getDesktop().open(tmpFile);
    }
}