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
    private Label lblErrorMsgID;

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
    private MFXTextField txtGuideID;

    @FXML
    private MFXTextField txtLastName;

    @FXML
    private MFXTextField txtPassword;

    @FXML
    private MFXTextField txtUsername;

    private String getUserName()
    {
        return txtUsername.getText();
    }

    private String getPassword()
    {
        return txtPassword.getText();
    }

    private String getEmail()
    {
        return txtEmail.getText();
    }

    private String getFirstName()
    {
        return txtFirstName.getText();
    }

    private String getLastName()
    {
        return txtLastName.getText();
    }

    private boolean isDetailsValid(String username, String email, String firstName, String lastName)
    {
        boolean retValue = true;
        // check if username exists with db - missing. Adir working here.
        if (!CommonUtils.isEmailAddressValid(email))
        {
            if (email.isEmpty())
                lblErrorMsgEmail.setText("Email is required");
            else lblErrorMsgEmail.setText("Email is invalid");
            retValue = false;
        }
        else lblErrorMsgEmail.setText("");

        if(!CommonUtils.isValidName(firstName))
        {
            if(firstName.isEmpty())
                lblErrorMsgFirstName.setText("First name is required");
            else lblErrorMsgFirstName.setText("First name is invalid, only letters allowed");
            retValue = false;
        }
        else lblErrorMsgFirstName.setText("");

        if (!CommonUtils.isValidName(lastName))
        {
            if (lastName.isEmpty())
                lblErrorMsgLastName.setText("Last name is required");
            else lblErrorMsgLastName.setText("Last name is invalid, only letters allowed");
            retValue = false;
        }

        return retValue;
    }

    @FXML
    void OnClickSubmitButton(ActionEvent event)
    {

    }

}