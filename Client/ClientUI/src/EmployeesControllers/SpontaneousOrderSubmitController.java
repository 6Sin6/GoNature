package EmployeesControllers;

import CommonClient.ClientUI;
import CommonClient.controllers.BaseController;
import CommonUtils.CommonUtils;
import CommonUtils.ConfirmationPopup;
import CommonUtils.MessagePopup;
import Entities.*;
import client.ClientCommunicator;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import javax.naming.CommunicationException;

public class SpontaneousOrderSubmitController extends BaseController {

    @FXML
    private Label errorLbl;
    @FXML
    private Text DetailsTxt;

    @FXML
    private MFXButton btnCreateOrder;

    @FXML
    private MFXTextField txtFirstName;

    @FXML
    private MFXTextField txtLastName;

    @FXML
    private MFXTextField txtNumOfVisitors;

    @FXML
    private MFXTextField txtPhone;

    @FXML
    private MFXTextField txtEmail;
    private Integer maxNumOfVisitors;
    private MessagePopup messageController;
    private String parkName;

    @FXML
    void OnClickCreateOrderButton(ActionEvent event) throws CommunicationException {

        if (!validateFields()) {
            return;
        }
        errorLbl.setText("");
        Order order = new Order("SPONTANEOUS", ParkBank.getUnmodifiableMap().get(parkName), null, txtEmail.getText(), txtPhone.getText(), OrderStatus.STATUS_SPONTANEOUS_ORDER_PENDING_PAYMENT, null, null, null, OrderType.ORD_TYPE_SINGLE, (CommonUtils.convertStringToInt(txtNumOfVisitors.getText())));
        Object msg = new Message(OpCodes.OP_CREATE_SPOTANEOUS_ORDER, applicationWindowController.getUser().getUsername(), order);
        ClientUI.client.accept(msg);
        Message respondMsg = ClientCommunicator.msg;
        OpCodes returnOpCode = respondMsg.getMsgOpcode();
        if(returnOpCode == OpCodes.OP_DB_ERR)
        {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        if(!(respondMsg.getMsgData() instanceof Order)) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        Order cnfrmorder = (Order) respondMsg.getMsgData();
        if (returnOpCode == OpCodes.OP_CREATE_SPOTANEOUS_ORDER) {
            String strForPopup = "The order " + cnfrmorder.getOrderID() + " has been created successfully";
            ConfirmationPopup confirmPopup = new ConfirmationPopup(strForPopup, () ->
            {
                applicationWindowController.loadEmployeesPage("GenerateBillPage");
                applicationWindowController.loadMenu(applicationWindowController.getUser());
                GenerateBillController generateBillController = (GenerateBillController) applicationWindowController.getCurrentActiveController();
                generateBillController.GenerateBillSpontaneousOrder(cnfrmorder.getOrderID());
            }
                    , 1000, 768, false, "OK", false);
            confirmPopup.show(applicationWindowController.getRoot());
        } else {
            String strForPopup = "Could not create order. Try again later.";
            ConfirmationPopup confirmPopup;
            confirmPopup = new ConfirmationPopup(strForPopup, () ->
            {

            }, 800, 400, false, "Yes", false);
            confirmPopup.show(applicationWindowController.getRoot());
        }
    }

    public boolean validateFields() {
        if (CommonUtils.anyStringEmpty(txtFirstName.getText(), txtLastName.getText(), txtPhone.getText(), txtEmail.getText(), txtNumOfVisitors.getText())) {
            errorLbl.setText("One or more fields are empty.");
            return false;
        }
        if (!CommonUtils.isValidPhone(txtPhone.getText())) {
            errorLbl.setText("Invalid phone. Please check your input.");
            return false;
        }
        if (!CommonUtils.isValidName(txtFirstName.getText()) || !CommonUtils.isValidName(txtLastName.getText()))
            errorLbl.setText("Please enter a valid first and last name.");
        if (!CommonUtils.isEmailAddressValid(txtEmail.getText())) {
            errorLbl.setText("Invalid email. Please check your input.");
            return false;
        }
        if (CommonUtils.convertStringToInt(txtNumOfVisitors.getText()) <= 0) {
            errorLbl.setText("Invalid number of visitors. Please check your input.");
            return false;
        }
        if (CommonUtils.convertStringToInt(txtNumOfVisitors.getText()) > maxNumOfVisitors) {
            errorLbl.setText("Out of park capacity. Please insert a smaller number of visitors.");
            return false;
        }
        return true;
    }

    public void start(String parkName, Integer maxNumOfVisitors) {
        this.parkName = parkName;
        DetailsTxt.setText("Park Name: " + this.parkName);
        this.maxNumOfVisitors = maxNumOfVisitors;
    }

    public void setMessagePopup(MessagePopup messageController) {
        this.messageController = messageController;
    }

    public void closePopup() {
        messageController.closePopup(false);
    }

}
