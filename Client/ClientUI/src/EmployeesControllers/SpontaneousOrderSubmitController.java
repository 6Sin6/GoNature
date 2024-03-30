package EmployeesControllers;

import CommonClient.ClientUI;
import CommonClient.controllers.BaseController;
import CommonUtils.CommonUtils;
import CommonUtils.ConfirmationPopup;
import CommonUtils.MessagePopup;
import Entities.*;
import client.ClientCommunicator;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import javax.naming.CommunicationException;

/**
 * This class represents the controller for submitting spontaneous orders.
 * It allows users to create new orders without prior reservation, providing necessary details such as visitor information
 * and preferences. The controller handles validation of input fields, creation of orders, and communication with the server
 * to process the order creation request. Upon successful order creation, it displays a confirmation popup and navigates
 * the user to the GenerateBillPage for further processing. This controller also initializes with park details and
 * maximum number of visitors allowed for efficient order submission.
 */

public class SpontaneousOrderSubmitController extends BaseController {

    /**
     * Label for displaying error messages. This can include validation errors
     * or other critical messages that need to be communicated to the user.
     */
    @FXML
    private Label errorLbl;

    /**
     * Text field for displaying detailed information about the selected park or reservation details.
     * This can be used to provide the user with feedback or instructions.
     */
    @FXML
    private Text DetailsTxt;

    /**
     * Button to create a new order based on the information entered in the form.
     * This triggers the reservation process and order confirmation.
     */
    @FXML
    private MFXButton btnCreateOrder;

    /**
     * TextField for the visitor's first name. This is part of the reservation form
     * where the user enters their personal information.
     */
    @FXML
    private TextField txtFirstName;

    /**
     * TextField for the visitor's last name. It's another component of the reservation form
     * for entering personal information.
     */
    @FXML
    private TextField txtLastName;

    /**
     * TextField for entering the number of visitors. This information is used to ensure
     * that the reservation does not exceed the park's capacity or the maximum number of visitors allowed.
     */
    @FXML
    private TextField txtNumOfVisitors;

    /**
     * TextField for the visitor's phone number. This is used for contact purposes
     * related to the reservation.
     */
    @FXML
    private TextField txtPhone;

    /**
     * TextField for the visitor's email address. This may be used for sending confirmation
     * emails or important updates about the reservation.
     */
    @FXML
    private TextField txtEmail;

    /**
     * The maximum number of visitors allowed for a single reservation. This value can be used
     * to validate the input in {@code txtNumOfVisitors} and ensure it does not exceed this limit.
     */
    private Integer maxNumOfVisitors;

    /**
     * Controller for displaying popup messages. This can be used for showing success, error,
     * or informational messages to the user in a standardized format.
     */
    private MessagePopup messageController;

    /**
     * The name of the park where the reservation is being made. This can be used to display
     * contextual information to the user and in the confirmation process.
     */
    private String parkName;

    /**
     * CheckBox indicating whether the group requires a guide. This is part of the reservation
     * form and can affect the reservation details and pricing.
     */
    @FXML
    private CheckBox groupGuideCheckBox;

    /**
     * Handles the action event triggered by clicking the "Create Order" button.
     * This method validates the input fields, creates a new order based on the provided information,
     * and sends a request to the server to create a spontaneous order.
     * Upon receiving the response from the server, it displays a confirmation popup with appropriate messages.
     * If successful, it navigates the user to the GenerateBillPage to generate a bill for the created order.
     *
     * @param event The ActionEvent triggered by clicking the "Create Order" button.
     * @throws CommunicationException if there is an error in communication with the server.
     */
    @FXML
    void OnClickCreateOrderButton(ActionEvent event) throws CommunicationException {

        if (!validateFields()) {
            return;
        }
        errorLbl.setText("");
        OrderType orderType = groupGuideCheckBox.isSelected() ? OrderType.ORD_TYPE_GROUP : OrderType.ORD_TYPE_SINGLE;
        Order order = new Order("SPONTANEOUS", ParkBank.getUnmodifiableMap().get(parkName), null, txtEmail.getText(), txtPhone.getText(), OrderStatus.STATUS_SPONTANEOUS_ORDER_PENDING_PAYMENT, null, null, null, orderType, (CommonUtils.convertStringToInt(txtNumOfVisitors.getText())));
        Object msg = new Message(OpCodes.OP_CREATE_SPOTANEOUS_ORDER, applicationWindowController.getUser().getUsername(), order);
        ClientUI.client.accept(msg);
        Message respondMsg = ClientCommunicator.msg;
        OpCodes returnOpCode = respondMsg.getMsgOpcode();
        if (returnOpCode == OpCodes.OP_DB_ERR) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        if (!(respondMsg.getMsgData() instanceof Order)) {
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

    /**
     * Validates the input fields for creating a new order.
     * This method checks if the required fields are not empty and if the input formats are valid.
     * It verifies the correctness of the phone number, email address, and the number of visitors.
     * Additionally, it ensures that the number of visitors does not exceed the maximum capacity of the park.
     *
     * @return {@code true} if all fields are valid; {@code false} otherwise.
     */
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

    /**
     * Initializes the controller with the park name and maximum number of visitors.
     * This method sets the park name and maximum number of visitors received as parameters,
     * and updates the UI accordingly to display the park details.
     *
     * @param parkName         The name of the park.
     * @param maxNumOfVisitors The maximum number of visitors allowed in the park.
     */
    public void start(String parkName, Integer maxNumOfVisitors) {
        this.parkName = parkName;
        DetailsTxt.setText("Park Name: " + this.parkName);
        this.maxNumOfVisitors = maxNumOfVisitors;
    }

    /**
     * Sets the message popup controller.
     * This method assigns the provided message popup controller to the local messageController variable.
     *
     * @param messageController The message popup controller to be set.
     */
    public void setMessagePopup(MessagePopup messageController) {
        this.messageController = messageController;
    }

    /**
     * Closes the popup window.
     * This method calls the closePopup method of the message controller to close the popup window.
     * It indicates that the popup is closed without any specific action taken.
     */
    public void closePopup() {
        messageController.closePopup(false);
    }

}
