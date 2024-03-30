package EmployeesControllers;

import CommonClient.ClientUI;
import CommonClient.Utils;
import CommonClient.controllers.BaseController;
import CommonUtils.ConfirmationPopup;
import Entities.Message;
import Entities.OpCodes;
import Entities.ParkDepartmentManager;
import Entities.ParkManager;
import client.ClientCommunicator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static CommonClient.Utils.getNumberFromMonthName;

/**
 * This class represents the controller for the view reports page.
 * It allows park department managers and park managers to select various parameters,
 * such as the report type, month, year, and park, and view corresponding reports.
 * The class handles user interactions with combo boxes to select report parameters,
 * fetches reports from the server, and displays them as PDF files.
 * Additionally, it provides methods to retrieve department IDs, park IDs, and park names,
 * as well as to update combo boxes based on user selections.
 * The class also contains utility methods to clean up UI state, handle selection change events,
 * and retrieve keys from values in maps.
 */

public class ViewReportsPageController extends BaseController {

    /**
     * The error message label used to display error messages related to report generation.
     */
    @FXML
    private Label errMsg;

    /**
     * The combo box for selecting the month of the report.
     */
    @FXML
    private ComboBox<String> monthCmb;

    /**
     * The combo box for selecting the park.
     */
    @FXML
    private ComboBox<String> parkCmb;

    /**
     * The combo box for selecting the type of report.
     */
    @FXML
    private ComboBox<String> reportCmb;

    /**
     * The label used to display the result of viewing a report.
     */
    @FXML
    private Label viewResultMsg;

    /**
     * The combo box for selecting the year of the report.
     */
    @FXML
    private ComboBox<String> yearCmb;

    /**
     * A map storing the names of parks.
     */
    private HashMap<String, String> parkNamesMap = null;

    /**
     * A flag indicating whether the current page is managed by a park manager.
     */
    private boolean parkManagerPage = false;


    /**
     * Clears all selections and resets the combo boxes and flags related to the UI state.
     * This method resets the error and information labels, clears the selections in the combo boxes,
     * clears the items in the combo boxes, and resets the flags related to the UI state.
     */
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
        parkNamesMap = null;
    }


    /**
     * Retrieves the department ID of the park department manager user.
     * This method retrieves the department ID from the park department manager user object stored in the application window controller.
     *
     * @return The department ID of the park department manager user.
     */
    private Integer getDepartmentID() {
        return ((ParkDepartmentManager) this.applicationWindowController.getUser()).getDepartmentID();
    }

    /**
     * Retrieves the ID of the park associated with the current park manager user.
     * This method retrieves the park ID from the park manager user object stored in the application window controller.
     *
     * @return The ID of the park associated with the current park manager user.
     */
    private String getParkID() {
        return ((ParkManager) applicationWindowController.getUser()).getParkID();
    }


    /**
     * Retrieves the name of the park associated with the current park ID.
     * This method sends a request to the server to fetch the name of the park associated with the current park ID.
     *
     * @return The name of the park, or null if an error occurs during retrieval.
     */
    private String getParkName() {
        String parkID = this.getParkID();
        Message message = new Message(OpCodes.OP_GET_PARK_NAME_BY_PARK_ID, applicationWindowController.getUser().getUsername(), parkID);
        ClientUI.client.accept(message);

        Message response = ClientCommunicator.msg;
        if (response.getMsgOpcode() != OpCodes.OP_GET_PARK_NAME_BY_PARK_ID)
            viewResultMsg.setText("Something went wrong... Please try again later");

        return (String) response.getMsgData();
    }

    /**
     * Retrieves the names of parks associated with the current department.
     * This method sends a request to the server to fetch the park names associated with the current department.
     * If the park names are not already cached, it retrieves them from the server response and caches them for future use.
     *
     * @return A map containing park IDs as keys and park names as values, or null if an error occurs during retrieval.
     */
    private Map<String, String> getParksNames() {
        if (parkNamesMap == null) {
            String departmentID = String.valueOf(this.getDepartmentID());
            Message msg = new Message(OpCodes.OP_GET_PARKS_BY_DEPARTMENT, applicationWindowController.getUser().getUsername(), departmentID);
            ClientUI.client.accept(msg);

            Message response = ClientCommunicator.msg;
            if (response.getMsgOpcode() == OpCodes.OP_DB_ERR) {
                ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
                confirmationPopup.show(applicationWindowController.getRoot());
                return null;
            }
            if (response.getMsgOpcode() != OpCodes.OP_GET_PARKS_BY_DEPARTMENT)
                viewResultMsg.setText("Something went wrong... Please try again later");

            //noinspection unchecked
            parkNamesMap = (HashMap<String, String>) response.getMsgData();
        }

        return parkNamesMap;
    }


    /**
     * Updates the month combo box with the specified number of months.
     * This method clears the existing items in the month combo box and adds the specified number of months to the combo box.
     * The months are added based on their names from January to the specified last month.
     *
     * @param lastMonth The index of the last month to be included in the combo box.
     */
    private void updateMonthComboBox(int lastMonth) {
        monthCmb.getItems().clear();
        String[] monthNames = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September",
                "October", "November", "December"};
        ArrayList<String> months = new ArrayList<>(Arrays.asList(monthNames).subList(0, lastMonth));
        monthCmb.getItems().addAll(months);
        monthCmb.setValue(monthCmb.getItems().get(0));
    }


    /**
     * Initializes the view for generating and viewing reports.
     * This method sets up the combo boxes for selecting the report type, year, and park, based on the user's role.
     * If the user is a park manager, the park combo box is disabled, and the report options are limited to park manager reports.
     * If the user is not a park manager, the park combo box is populated with park names, and both park and department reports are available.
     * The method also sets up event listeners for the combo boxes to handle user selections.
     */
    public void start() {
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
            if (applicationWindowController.getUser() instanceof ParkManager) {
                parkManagerPage = true;
                parkCmb.setValue(this.getParkName());
                parkCmb.setDisable(true);
            } else {
                Map<String, String> map = this.getParksNames();
                if (map == null) {
                    cleanup();
                    return;
                }
                parkCmb.getItems().addAll(map.values());
                parkCmb.setValue("All Parks");
                parkCmb.setDisable(true);
                reportCmb.getItems().addAll(Utils.departmentReportsMap.keySet());
            }
        } catch (Exception e) {
            errMsg.setText("Something went wrong... Please try again later");
            this.cleanup();
            return;
        }
        reportCmb.getItems().addAll(Utils.parkManagerReportsMap.keySet());
        reportCmb.setValue(reportCmb.getItems().get(0));
        reportCmb.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> onSelectReport(oldVal, newVal));
        yearCmb.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> onSelectYear(oldVal, newVal));
    }


    /**
     * Handles the selection change event when choosing a year from the year combo box.
     * This method updates the month combo box based on the selected year.
     * If the selected year is the current year, it updates the month combo box with the months up to the current month.
     * If the selected year is not the current year, it updates the month combo box with all twelve months.
     *
     * @param oldValue The previously selected year value.
     * @param newValue The newly selected year value.
     */
    private void onSelectYear(String oldValue, String newValue) {
        if (oldValue == null || newValue == null)
            return;
        int oldVal = Integer.parseInt(oldValue), newVal = Integer.parseInt(newValue);
        int currentYear = java.time.Year.now().getValue();
        if (oldVal == newVal || (oldVal != currentYear && newVal != currentYear))
            return;
        if (newVal == currentYear) {
            updateMonthComboBox(LocalDate.now().getMonthValue());
            monthCmb.getSelectionModel().selectLast();
        } else updateMonthComboBox(12);
    }


    /**
     * Handles the selection change event when choosing a report from the report combo box.
     * This method adjusts the state of the park combo box based on the selected report.
     * If the selected report is a park manager report, it enables the park combo box for park selection.
     * If the selected report is a department report, it disables the park combo box and sets its value to "All Parks".
     * If the user is a park manager or if the selected report type remains within park manager reports, no action is taken.
     *
     * @param oldVal The previously selected report value.
     * @param newVal The newly selected report value.
     */
    void onSelectReport(String oldVal, String newVal) {
        if (parkManagerPage ||
                (Utils.parkManagerReportsMap.containsKey(newVal) && Utils.parkManagerReportsMap.containsKey(oldVal)))
            return;

        if (Utils.parkManagerReportsMap.containsKey(newVal)) { // park reports
            parkCmb.setValue(parkCmb.getItems().get(0));
            parkCmb.setDisable(false);
        } else { // department reports
            parkCmb.setDisable(true);
            parkCmb.setValue("All Parks");
        }
    }


    /**
     * Handles the action event triggered by clicking the "View Report" button.
     * This method retrieves the selected report parameters, such as the report type, month, year, and park ID or department ID,
     * and sends a message to the server to fetch the corresponding report from the database.
     * Upon receiving the report data, it displays the report as a PDF file or shows an error message if the report is not found or if there are any errors.
     *
     * @param ignoredEvent The ActionEvent triggered by clicking the "View Report" button (ignored).
     */
    @FXML
    void onClickViewReport(ActionEvent ignoredEvent) {
        String selectedReport = reportCmb.getValue();
        boolean isDepartmentReport = Arrays.asList(Utils.departmentReports).contains(selectedReport);

        // Load PDF Blob from DB.
        String selectedMonth = monthCmb.getValue();
        String selectedYear = yearCmb.getValue();

        errMsg.setText("");
        String bodyId = "";

        // Park manager cannot select a department report, this is why the else clause is valid.
        // Nothing here is a best practice, sue me...
        if (!parkManagerPage && isDepartmentReport) {
            bodyId = String.valueOf(((ParkDepartmentManager) applicationWindowController.getUser()).getDepartmentID());
        } else if (!isDepartmentReport) {
            if (!parkManagerPage) {
                Map<String, String> map = this.getParksNames();
                if (map == null) {
                    cleanup();
                    errMsg.setText("error getting park names");
                    return;
                }
                bodyId = this.getKeyFromValue(map, parkCmb.getValue());
            } else bodyId = this.getParkID();
        }

        String[] params;
        try {
            params = new String[]{
                    String.valueOf(isDepartmentReport),
                    isDepartmentReport ? Utils.departmentReportsMap.get(selectedReport) : Utils.parkManagerReportsMap.get(selectedReport),
                    String.valueOf(getNumberFromMonthName(selectedMonth)),
                    selectedYear,
                    bodyId
            };
        } catch (ParseException e) {
            errMsg.setText("Error");
            this.cleanup();
            return;
        }
        Message msg = new Message(OpCodes.OP_VIEW_REPORT_BLOB, applicationWindowController.getUser().getUsername(), params);
        ClientUI.client.accept(msg);

        Message response = ClientCommunicator.msg;
        if (response.getMsgOpcode() != OpCodes.OP_VIEW_REPORT_BLOB)
            viewResultMsg.setText("Something went wrong... Please try again later");

        byte[] pdfBlob = (byte[]) response.getMsgData();
        errMsg.setText("");
        if (pdfBlob == null) {
            viewResultMsg.setText("No " + selectedReport + " report found for the selected month and year.");
            return;
        } else viewResultMsg.setText("");

        String reportName = selectedReport + " Report" + "_" + selectedYear + "_" + selectedMonth + "_" + parkCmb.getValue();
        File tmpFile;
        try {
            tmpFile = File.createTempFile(reportName, ".pdf");
        } catch (IOException e) {
            this.cleanup();
            errMsg.setText("error creating temporary file");
            return;
        }
        tmpFile.deleteOnExit();

        // Write the PDF content to the temp file
        try (FileOutputStream fos = new FileOutputStream(tmpFile)) {
            fos.write(pdfBlob);
        } catch (IOException e) {
            this.cleanup();
            errMsg.setText("Error writing to file");
            return;
        }

        try {
            Desktop.getDesktop().open(tmpFile);
        } catch (IOException e) {
            this.cleanup();
            errMsg.setText("Error opening file");
        }
    }


    /**
     * Retrieves the key associated with a specified value in a given map.
     * This method iterates through the entries of the map to find the key corresponding to the provided value.
     * If the value is found, the corresponding key is returned; otherwise, it throws an IllegalArgumentException.
     *
     * @param map   The map to search for the value.
     * @param value The value for which the corresponding key is to be retrieved.
     * @return The key associated with the specified value.
     * @throws IllegalArgumentException if the specified value is not found in the map.
     */
    private String getKeyFromValue(Map<String, String> map, String value) {
        for (Map.Entry<String, String> entry : map.entrySet())
            if (entry.getValue().equals(value))
                return entry.getKey();
        throw new IllegalArgumentException("Error getting park ID!");
    }
}