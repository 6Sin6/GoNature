package EmployeesControllers;

import CommonClient.ClientUI;
import CommonClient.Utils;
import CommonClient.controllers.BaseController;
import Entities.*;
import client.ClientCommunicator;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import java.time.LocalDate;
import javax.naming.CommunicationException;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.List;

import static CommonClient.Utils.getNumberFromMonthName;

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
        parkCmb.getItems().clear();
        reportCmb.getItems().clear();
        monthCmb.getItems().clear();
        yearCmb.getItems().clear();
        parkManagerPage = false;
    }

    private Integer getDepartmentID()
    {
        return ((ParkDepartmentManager)this.applicationWindowController.getUser()).getDepartmentID();
    }

    private String getParkID()
    {
        return ((ParkManager) applicationWindowController.getUser()).getParkID();
    }

    private String getParkName() throws CommunicationException
    {
        String parkID = this.getParkID();
        Message message = new Message(OpCodes.OP_GET_PARK_NAME_BY_PARK_ID, applicationWindowController.getUser().getUsername(), parkID);
        ClientUI.client.accept(message);

        Message response = ClientCommunicator.msg;
        if (response.getMsgOpcode() != OpCodes.OP_GET_PARK_NAME_BY_PARK_ID) {
            viewResultMsg.setText("Something went wrong... Please try again later");
            throw new CommunicationException("Failed to load report.");
        }
        return (String) response.getMsgData();
    }

    private HashMap<String, String> parkNamesMap = null;
    private Map<String, String> getParksNames() throws CommunicationException
    {
        if (parkNamesMap == null)
        {
            String departmentID = String.valueOf(this.getDepartmentID());
            Message msg = new Message(OpCodes.OP_GET_PARKS_BY_DEPARTMENT, applicationWindowController.getUser().getUsername(), departmentID);
            ClientUI.client.accept(msg);

            Message response = ClientCommunicator.msg;
            if (response.getMsgOpcode() != OpCodes.OP_GET_PARKS_BY_DEPARTMENT) {
                viewResultMsg.setText("Something went wrong... Please try again later");
                throw new CommunicationException("Failed to load report.");
            }
            parkNamesMap = (HashMap<String, String>) response.getMsgData();
        }

        return parkNamesMap;
    }

    private void updateMonthComboBox(int lastMonth)
    {
        monthCmb.getItems().clear();
        String[] monthNames = new String[] {"January", "February", "March", "April", "May", "June", "July", "August", "September",
                "October", "November", "December"};
        ArrayList<String> months = new ArrayList<>();
        for (int i = 0; i < lastMonth; i++)
            months.add(monthNames[i]);
        monthCmb.getItems().addAll(months);
        monthCmb.setValue(monthCmb.getItems().get(0));
    }

    public void start()
    {
        // Definitions
        ArrayList<String> years = new ArrayList<>();
        int earliestYear = 2021;
        int currentYear = java.time.Year.now().getValue();
        int currentMonth = LocalDate.now().getMonthValue();

        // Years combo box:
        for (int i = earliestYear; i <= currentYear; i++)
            years.add(String.valueOf(i));
        yearCmb.getItems().addAll(years);
        yearCmb.setValue(yearCmb.getItems().get(currentYear - earliestYear));

        // Month combo box:
        this.updateMonthComboBox(currentMonth);
        monthCmb.getSelectionModel().selectLast();


        try {
            if (applicationWindowController.getUser() instanceof ParkManager)
            {
                parkManagerPage = true;
                parkCmb.setValue(this.getParkName());
                parkCmb.setDisable(true);
            } else
            {
                Map<String, String> map = this.getParksNames();
                for (int i = 1; i <= map.size(); i++)
                {
                    String id = String.valueOf(i);
                    parkCmb.getItems().add(map.get(id));
                }
                parkCmb.setValue("All Parks");
                reportCmb.getItems().addAll(Utils.departmentReportsMap.keySet());
            }
        }
        catch (Exception e)
        {
            errMsg.setText("Something went wrong... Please try again later");
            this.cleanup();
            return;
        }
        reportCmb.getItems().addAll(Utils.parkManagerReportsMap.keySet());
        reportCmb.setValue(reportCmb.getItems().get(0));
        reportCmb.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> onSelectReport(oldVal, newVal));
        yearCmb.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> onSelectYear(oldVal, newVal));
    }

    private void onSelectYear(String oldValue, String newValue)
    {
        if (oldValue == null || newValue == null)
            return;
        int oldVal = Integer.parseInt(oldValue), newVal = Integer.parseInt(newValue);
        int currentYear = java.time.Year.now().getValue();
        if (oldVal == newVal || (oldVal != currentYear && newVal != currentYear))
            return;
        if (newVal == currentYear)
        {
            updateMonthComboBox(LocalDate.now().getMonthValue());
            monthCmb.getSelectionModel().selectLast();
        }
        else updateMonthComboBox(12);
    }

    void onSelectReport(String oldVal, String newVal)
    {
        if (parkManagerPage ||
                (Utils.parkManagerReportsMap.containsKey(newVal) && Utils.parkManagerReportsMap.containsKey(oldVal)))
            return;

        if (Utils.parkManagerReportsMap.containsKey(newVal))
        { // park reports
            parkCmb.setValue(parkCmb.getItems().get(0));
            parkCmb.setDisable(false);
        } else
        { // department reports
            parkCmb.setDisable(true);
            parkCmb.setValue("All Parks");
        }
    }

    @FXML
    void onClickViewReport(ActionEvent event) throws IOException, CommunicationException, ParseException
    {
        String selectedReport = reportCmb.getValue();
        boolean isDepartmentReport = Arrays.asList(Utils.departmentReports).contains(selectedReport);

        // Load PDF Blob from DB.
        String selectedMonth = monthCmb.getValue();
        String selectedYear = yearCmb.getValue();

        errMsg.setText("");
        String bodyId = "";
        // Park manager cannot select a department report, this is why the else clause is valid.
        // Nothing here is a best practice, sue me...
        if (!parkManagerPage && isDepartmentReport)
        {
            bodyId = String.valueOf(((ParkDepartmentManager) applicationWindowController.getUser()).getDepartmentID());
        } else if (!isDepartmentReport)
        {
            if (!parkManagerPage)
            {
                String selection = parkCmb.getValue();
                bodyId = this.getKeyFromValue(this.getParksNames(), parkCmb.getValue());
            }
            else bodyId = this.getParkID();
        }

        String[] params = new String[]{
                String.valueOf(isDepartmentReport),
                isDepartmentReport ? Utils.departmentReportsMap.get(selectedReport) : Utils.parkManagerReportsMap.get(selectedReport),
                String.valueOf(getNumberFromMonthName(selectedMonth)),
                selectedYear,
                bodyId
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
        else viewResultMsg.setText("");

        String reportName = selectedReport + " Report" + "_" + selectedYear + "_" + selectedMonth + "_" + parkCmb.getValue();
        File tmpFile = File.createTempFile(reportName, ".pdf");
        tmpFile.deleteOnExit();

        // Write the PDF content to the temp file
        try (FileOutputStream fos = new FileOutputStream(tmpFile)) {
            fos.write(pdfBlob);
        }

        Desktop.getDesktop().open(tmpFile);
    }

    private static String getKeyFromValue(Map<String, String> map, String value)
    {
        for (Map.Entry<String, String> entry : map.entrySet())
            if (entry.getValue().equals(value))
                return entry.getKey();
        throw new IllegalArgumentException("Error getting park ID!");
    }
}