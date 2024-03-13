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
    private Label lblErrorMsg;

    @FXML
    private MFXTextField txtGroupGuideID;

    @FXML
    void OnClickSubmitButton(ActionEvent event)
    {
        String groupGuideID = txtGroupGuideID.getText();

        if(groupGuideID != null && groupGuideID.isEmpty())
        {
            lblErrorMsg.setText("Please enter a group guide ID");
            return;
        }

        //if(!CommonUtils.isValidID(groupGuideID))
        //{
         //   lblErrorMsg.setText("Invalid group guide ID, must be 9 digits.");
         //   return;
        //}

        Message msg = new Message(OpCodes.OP_REGISTER_GROUP_GUIDE, super.applicationWindowController.getUser().getUsername(), groupGuideID);
        ClientUI.client.accept(msg);
        Message respondMsg = ClientCommunicator.msg;

        switch(respondMsg.getMsgOpcode())
        {
            case OP_VISITOR_ID_DOESNT_EXIST:
                lblErrorMsg.setText("Visitor ID does not exist!");
                break;
            case OP_VISITOR_IS_ALREADY_GROUP_GUIDE:
                lblErrorMsg.setText("Visitor is already group guide!");
                break;
            case OP_UPDATED_VISITOR_TO_GROUP_GUIDE:
                MessagePopup popup = new MessagePopup("Group Guide Registered Successfully!", Duration.seconds(3), 500, 300, false);
                popup.show(applicationWindowController.getRoot());
                applicationWindowController.setCenterPage("/EmployeesUI/SupportRepresentativeDashboardPage.fxml");
                break;
            default:
                lblErrorMsg.setText("Unknown Error occurred, please try again later.");
                break;

        }
    }

}
