package EmployeesControllers;

import CommonClient.ClientUI;
import CommonClient.controllers.BaseController;
import CommonUtils.CommonUtils;
import CommonUtils.MessagePopup;
import Entities.Message;
import Entities.OpCodes;
import client.ClientCommunicator;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class RegisterGroupGuideController extends BaseController {

    @FXML
    private MFXButton btnSubmit;

    @FXML
    private Label lblErrorMsgEmail;

    @FXML
    private Label lblErrorMsgFirstName;

    @FXML
    private Label lblErrorMsgGeneral;

    @FXML
    private Label lblErrorMsgLastName;

    @FXML
    private Label lblErrorMsgPassword;

    @FXML
    private Label lblErrorMsgUsername;

    @FXML
    private MFXTextField txtEmail;

    @FXML
    private MFXTextField txtFirstName;

    @FXML
    private MFXTextField txtLastName;

    @FXML
    private MFXTextField txtPassword;

    @FXML
    private MFXTextField txtUsername;

    @FXML
    void OnClickSubmitButton(ActionEvent event)
    {

    }

}