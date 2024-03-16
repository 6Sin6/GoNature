package VisitorsControllers;

import CommonClient.ClientUI;
import CommonClient.controllers.BaseController;
import CommonUtils.CommonUtils;
import Entities.*;
import CommonUtils.ConfirmationPopup;
import client.ClientCommunicator;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import javax.naming.CommunicationException;

import javafx.event.ActionEvent;

import java.net.URL;
import java.sql.Timestamp;
import java.util.ResourceBundle;


public class UpdateOrderDetailsPageController extends BaseController implements Initializable {

    @FXML
    private Label DateLabel;

    @FXML
    private TextField EmailText;

    @FXML
    private Label OrderIDText;

    @FXML
    private Label ParkName;

    @FXML
    private TextField PhoneText;

    @FXML
    private Label TimeLabel;

    @FXML
    private MFXButton UpdateBtn;

    @FXML
    private Pane btnHandleOrder;

    @FXML
    private MFXButton cancelBtn;

    @FXML
    private Label errorLabel;

    @FXML
    private Text txtHeader;


    private Order order;


    public void setFields(Order o1) {
        errorLabel.setText("");
        this.order = o1;
        OrderIDText.setText(String.valueOf(order.getOrderID()));
        ParkName.setText(ParkBank.getParkNameByID(order.getParkID()));
        EmailText.setText(order.getClientEmailAddress());
        PhoneText.setText(order.getPhoneNumber());
        Timestamp timestamp = order.getVisitationDate();
        String date = CommonClient.Utils.parseVisitDate(timestamp);
        String time = CommonClient.Utils.parseVisitTime(timestamp);
        PhoneText.setText(order.getPhoneNumber());
        EmailText.setText(order.getClientEmailAddress());
        DateLabel.setText(date);
        TimeLabel.setText(time);
    }

    @FXML
    public void OnClickUpdateBtn(ActionEvent actionEvent) throws CommunicationException {
        if (!validateFields()) {
            return;
        }
        String[] arrForMsg = new String[3];
        arrForMsg[0] = String.valueOf(order.getOrderID());
        arrForMsg[1] = PhoneText.getText();
        arrForMsg[2] = EmailText.getText();
        User user = applicationWindowController.getUser();
        if (user instanceof SingleVisitor) {
            Object msg = new Message(OpCodes.OP_UPDATE_ORDER_DETAILS_BY_ORDERID, user.getUsername(), arrForMsg);
            ClientUI.client.accept(msg);
            Message respondMsg = ClientCommunicator.msg;
            if (respondMsg.getMsgOpcode() != OpCodes.OP_UPDATE_ORDER_DETAILS_BY_ORDERID) {
                throw new CommunicationException("Respond not appropriate from server");
            }
            String strForPopup = "The order has been updated successfully";
            ConfirmationPopup confirmPopup = new ConfirmationPopup(strForPopup, () ->
            {
                applicationWindowController.loadDashboardPage(applicationWindowController.getUser().getRole());
                applicationWindowController.loadMenu(applicationWindowController.getUser());
            }
                    , 600, 300, false, "OK", false);
            confirmPopup.show(applicationWindowController.getRoot());
        } else if (user instanceof VisitorGroupGuide) {
            Object msg = new Message(OpCodes.OP_UPDATE_GROUP_ORDER_DETAILS_BY_ORDERID, user.getUsername(), order);
            ClientUI.client.accept(msg);
            Message respondMsg = ClientCommunicator.msg;
            if (respondMsg.getMsgOpcode() != OpCodes.OP_UPDATE_GROUP_ORDER_DETAILS_BY_ORDERID) {
                throw new CommunicationException("Respond not appropriate from server");
            }
            String strForPopup = "The order details has been updated successfully";
            ConfirmationPopup confirmPopup = new ConfirmationPopup(strForPopup, () ->
            {
                applicationWindowController.loadDashboardPage(applicationWindowController.getUser().getRole());
                applicationWindowController.loadMenu(applicationWindowController.getUser());
            }
                    , 600, 300, false, "OK", false);
            confirmPopup.show(applicationWindowController.getRoot());
        } else {
            throw new CommunicationException("User not appropriate");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    private boolean validateFields() {
        if (EmailText.getText().isEmpty() || PhoneText.getText().isEmpty()) {
            errorLabel.setText("Please fill all the fields");
            return false;
        }
        if (!CommonUtils.isEmailAddressValid(EmailText.getText())) {
            errorLabel.setText("invalid Email address");
            return false;
        }
        if (!CommonUtils.isValidPhone((PhoneText.getText()))) {
            errorLabel.setText("invalid Phone number");
            return false;
        }
        return true;
    }

    public void OnClickCancelOrderBtn(ActionEvent actionEvent) throws CommunicationException {
        User user = applicationWindowController.getUser();
        Object msg = new Message(OpCodes.OP_HANDLE_VISITATION_CANCEL_ORDER, user.getUsername(), this.order);
        ClientUI.client.accept(msg);
        Message respondMsg = ClientCommunicator.msg;
        if (respondMsg.getMsgOpcode() != OpCodes.OP_HANDLE_VISITATION_CANCEL_ORDER) {
            throw new CommunicationException("Respond not appropriate from server");
        }
        String strForPopup = "The order has been canceled successfully";
        ConfirmationPopup confirmPopup = new ConfirmationPopup(strForPopup, () ->
        {
            applicationWindowController.loadDashboardPage(applicationWindowController.getUser().getRole());
            applicationWindowController.loadMenu(applicationWindowController.getUser());
        }
                , 600, 300, false, "OK", false);
        confirmPopup.show(applicationWindowController.getRoot());
    }

    @Override//need to check if we need it or not.
    public void cleanup() {
        super.cleanup();
    }
}
