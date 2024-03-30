package EmployeesControllers;

import CommonClient.ClientUI;
import CommonClient.controllers.BaseController;
import CommonUtils.CommonUtils;
import CommonUtils.ConfirmationPopup;
import Entities.*;
import client.ClientCommunicator;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyComboBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

import static CommonUtils.CommonUtils.convertStringToTimestamp;
import static CommonUtils.CommonUtils.convertTimestampToMinutes;

/**
 * This class represents the controller for setting park parameters.
 * It allows park managers to modify parameters such as maximum visitation time, gap between orders and visitors capacity,
 * and park capacity. The controller communicates with the server to retrieve park details, validate user input,
 * and submit requests to update park parameters. It provides methods for initializing the controller, cleaning up UI components,
 * retrieving park parameters, and handling the submission of parameter modification requests.
 */

public class RequestSettingParkParametersController extends BaseController implements Initializable {

    /**
     * Represents a JavaFX label used for displaying error messages.
     */
    @FXML
    private Label lblErrorMsg;

    /**
     * Represents a JavaFX label used for displaying success messages.
     */
    @FXML
    private Label lblSuccessMsg;

    /**
     * Represents a JavaFX text field used for entering the difference between orders and visitors capacity.
     */
    @FXML
    private TextField txtDifferenceOrdersVisitors;

    /**
     * Represents a JavaFX combo box used for selecting the maximum visitation time.
     */
    @FXML
    private MFXLegacyComboBox<String> cmbMaxVisitation;

    /**
     * Represents a JavaFX text field used for entering the park capacity.
     */
    @FXML
    private TextField txtParkCapacity;

    /**
     * Represents the Park object associated with the controller.
     */
    private Park park;

    /**
     * Initializes the controller after its root element has been completely processed.
     * This method is automatically called by the FXMLLoader when loading the FXML file.
     * It populates the dropdown menu for selecting maximum visitation time with values ranging from 1 to 8 hours.
     *
     * @param location  The location used to resolve relative paths for the root object, or {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for (int i = 1; i <= 8; i++) {
            cmbMaxVisitation.getItems().add(String.valueOf(i));
        }
    }


    /**
     * Resets the UI components related to park parameter modification to their default states.
     * This method clears the error messages, success messages, and input fields for modifying park parameters.
     * It is typically used to clean up the UI after a form submission or when resetting the input fields.
     */
    public void cleanup() {
        lblErrorMsg.setText("");
        lblSuccessMsg.setText("");
        txtDifferenceOrdersVisitors.setText("");
        cmbMaxVisitation.setValue("");
        txtParkCapacity.setText("");
    }


    /**
     * Retrieves park parameters for the current park manager user and populates the corresponding fields in the UI.
     * This method retrieves park details using the park ID associated with the park manager user.
     * It sends a request to the server to fetch park details and updates the UI fields with the retrieved parameters.
     * If successful, it sets the retrieved park details to the park manager user and updates the UI accordingly.
     */
    public void getParkParameters() {
        ParkManager user = (ParkManager) applicationWindowController.getUser();
        Message msg, response;

        // Populate Park for the department ID, in order to create a request for the department.
        String ParkID = user.getParkID();
        msg = new Message(OpCodes.OP_GET_PARK_DETAILS_BY_PARK_ID, applicationWindowController.getUser().getUsername(), ParkID);

        ClientUI.client.accept(msg);

        response = ClientCommunicator.msg;
        OpCodes returnOpCode = response.getMsgOpcode();
        if (returnOpCode == OpCodes.OP_DB_ERR) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        // Checking if the response from the server is inappropriate.
        if (returnOpCode != OpCodes.OP_GET_PARK_DETAILS_BY_PARK_ID) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        if (!(response.getMsgData() instanceof Park)) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }

        park = (Park) response.getMsgData();
        // Might as well set the park to the user.
        user.setPark(park);


        int localDateTime = park.getDefaultVisitationTime().toLocalDateTime().getHour();
        cmbMaxVisitation.setValue(String.valueOf(localDateTime));
        txtParkCapacity.setText(String.valueOf(park.getCapacity()));
        txtDifferenceOrdersVisitors.setText(String.valueOf(park.getGapVisitorsCapacity()));
    }


    /**
     * Handles the action event triggered by clicking the "SubmitButton" for updating park parameters.
     * This method processes the input provided by the user for updating park parameters such as maximum visitation time,
     * gap between orders and visitors capacity, and park capacity. It validates the input and sends requests to the department
     * for updating the park parameters. Upon successful submission, it displays a success message.
     *
     * @param ignoredEvent The ActionEvent triggered by clicking the "SubmitButton".
     */
    @FXML
    void OnClickSubmitButton(ActionEvent ignoredEvent) {
        lblSuccessMsg.setText("");
        lblErrorMsg.setText("");
        boolean maxVisitRequest = !Objects.equals(cmbMaxVisitation.getValue(), "");
        String gapOrdersAndVisitors = txtDifferenceOrdersVisitors.getText();
        Timestamp maxVisitationLongevity = null;

        Date tmpDate = new Date(System.currentTimeMillis());

        if (maxVisitRequest) {
            maxVisitationLongevity = convertStringToTimestamp(tmpDate.toString(), String.format("%02d:00", Integer.parseInt(cmbMaxVisitation.getValue())));
        }
        String parkCapacity = txtParkCapacity.getText();

        if (gapOrdersAndVisitors.isEmpty() && !maxVisitRequest && parkCapacity.isEmpty()) {
            lblErrorMsg.setText("Please fill at least one of the fields");
            return;
        }

        if ((!gapOrdersAndVisitors.isEmpty() && !gapOrdersAndVisitors.matches("[0-9]+")) ||
                (!parkCapacity.isEmpty() && !parkCapacity.matches("[0-9]+"))) {
            lblErrorMsg.setText("Please enter only numbers");
            return;
        }

        if ((!gapOrdersAndVisitors.isEmpty() && Integer.parseInt(gapOrdersAndVisitors) < 0) ||
                (!parkCapacity.isEmpty() && Integer.parseInt(parkCapacity) < 0)) {
            lblErrorMsg.setText("Please enter only positive numbers");
            return;
        }

        if (!gapOrdersAndVisitors.isEmpty() && Integer.parseInt(gapOrdersAndVisitors) > Integer.parseInt(parkCapacity)) {
            lblErrorMsg.setText("Difference between the number of orders and visitors capacity must be lesser than the park capacity.");
            return;
        }


        Message msg, response;
        // Create a map of the requests to be submitted to the department.
        Map<ParkParameters, RequestChangingParkParameters> requestMap = new HashMap<>();
        if (!gapOrdersAndVisitors.isEmpty() && park.getGapVisitorsCapacity() != Double.parseDouble(gapOrdersAndVisitors)) {
            requestMap.put(ParkParameters.PARK_GAP_VISITORS_CAPACITY, new RequestChangingParkParameters(park, ParkParameters.PARK_GAP_VISITORS_CAPACITY, Double.parseDouble(gapOrdersAndVisitors)));
        }
        if (maxVisitRequest && !convertTimestampToMinutes(maxVisitationLongevity).equals(convertTimestampToMinutes(park.getDefaultVisitationTime()))) {
            requestMap.put(ParkParameters.PARK_DEFAULT_MAX_VISITATION_LONGEVITY, new RequestChangingParkParameters(park, ParkParameters.PARK_DEFAULT_MAX_VISITATION_LONGEVITY, Double.parseDouble(convertTimestampToMinutes(maxVisitationLongevity).toString())));
        }
        if (!parkCapacity.isEmpty() && park.getCapacity() != Double.parseDouble(parkCapacity)) {
            requestMap.put(ParkParameters.PARK_CAPACITY, new RequestChangingParkParameters(park, ParkParameters.PARK_CAPACITY, Double.parseDouble(parkCapacity)));
        }

        if (requestMap.isEmpty()) {
            return;
        }

        msg = new Message(OpCodes.OP_SUBMIT_REQUESTS_TO_DEPARTMENT, applicationWindowController.getUser().getUsername(), requestMap);
        ClientUI.client.accept(msg);

        response = ClientCommunicator.msg;
        OpCodes returnOpCode = response.getMsgOpcode();
        if (returnOpCode == OpCodes.OP_DB_ERR) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        // Checking if the response from the server is inappropriate.
        if (returnOpCode != OpCodes.OP_SUBMIT_REQUESTS_TO_DEPARTMENT) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }

        lblSuccessMsg.setText("Your requests have been submitted successfully!");
    }


}
