package EmployeesControllers;

import CommonClient.ClientUI;
import CommonClient.controllers.BaseController;
import Entities.*;
import client.ClientCommunicator;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyComboBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import static CommonClient.Utils.convertStringToTimestamp;
import static CommonUtils.CommonUtils.convertTimestampToMinutes;

public class RequestSettingParkParametersController extends BaseController {
    @FXML
    private MFXButton btnSubmit;

    @FXML
    private Label lblErrorMsg;

    @FXML
    private Label lblSuccessMsg;

    @FXML
    private MFXTextField txtDifferenceOrdersVisitors;

    @FXML
    private MFXLegacyComboBox<String> txtMaxVisitation;

    @FXML
    private MFXTextField txtParkCapacity;

    @FXML
    void OnClickSubmitButton(ActionEvent event) {
        String gapOrdersAndVisitors = txtDifferenceOrdersVisitors.getText();

        if (txtMaxVisitation.getValue() == null) {
            txtMaxVisitation.setValue("");
        }

        Date tmpDate = new Date(System.currentTimeMillis());
        Timestamp maxVisitationLongevity = convertStringToTimestamp(tmpDate.toString(), txtMaxVisitation.getValue());
        String parkCapacity = txtParkCapacity.getText();

        if (gapOrdersAndVisitors.isEmpty() && txtMaxVisitation.getValue().isEmpty() && parkCapacity.isEmpty()) {
            lblErrorMsg.setText("Please fill at least one of the fields");
            return;
        }

        if (!gapOrdersAndVisitors.matches("[0-9]+") || !parkCapacity.matches("[0-9]+")) {
            lblErrorMsg.setText("Please enter only numbers");
            return;
        }

        if (Integer.parseInt(gapOrdersAndVisitors) < 0 || Integer.parseInt(parkCapacity) < 0) {
            lblErrorMsg.setText("Please enter only positive numbers");
            return;
        }

        ParkManager user = (ParkManager) applicationWindowController.getUser();
        Message msg, response;

        // Populate Park for the department ID, in order to create a request for the department.
        String ParkID = user.getParkID();
        msg = new Message(OpCodes.OP_GET_PARK_DETAILS_BY_PARK_ID, applicationWindowController.getUser().getUsername(), ParkID);

        ClientUI.client.accept(msg);

        response = ClientCommunicator.msg;
        if (response.getMsgOpcode() != OpCodes.OP_GET_PARK_DETAILS_BY_PARK_ID) {
            lblErrorMsg.setText("Something went wrong... Try again later");
            return;
        }

        Park park = (Park) response.getMsgData();
        // Might as well set the park to the user.
        user.setPark(park);

        // Create a map of the requests to be submitted to the department.
        Map<ParkParameters, RequestChangingParkParameters> requestMap = new HashMap<>();
        requestMap.put(ParkParameters.PARK_GAP_VISITORS_CAPACITY, new RequestChangingParkParameters(park, ParkParameters.PARK_GAP_VISITORS_CAPACITY, Double.parseDouble(gapOrdersAndVisitors)));
        requestMap.put(ParkParameters.PARK_DEFAULT_MAX_VISITATION_LONGEVITY, new RequestChangingParkParameters(park, ParkParameters.PARK_DEFAULT_MAX_VISITATION_LONGEVITY, Double.parseDouble(convertTimestampToMinutes(maxVisitationLongevity).toString())));
        requestMap.put(ParkParameters.PARK_CAPACITY, new RequestChangingParkParameters(park, ParkParameters.PARK_CAPACITY, Double.parseDouble(parkCapacity)));

        msg = new Message(OpCodes.OP_SUBMIT_REQUESTS_TO_DEPARTMENT, applicationWindowController.getUser().getUsername(), requestMap);
        ClientUI.client.accept(msg);

        response = ClientCommunicator.msg;
        if (response.getMsgOpcode() != OpCodes.OP_SUBMIT_REQUESTS_TO_DEPARTMENT) {
            lblErrorMsg.setText("Something went wrong... Try again later");
            return;
        }

        lblSuccessMsg.setText("Your requests have been submitted successfully!");
    }
}
