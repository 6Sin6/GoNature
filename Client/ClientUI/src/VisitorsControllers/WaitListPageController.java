package VisitorsControllers;

import CommonClient.ClientUI;
import CommonClient.controllers.BaseController;
import CommonUtils.CommonUtils;
import CommonUtils.ConfirmationPopup;
import Entities.*;
import client.ClientCommunicator;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.Timestamp;
import java.util.ResourceBundle;

import static CommonUtils.CommonUtils.parseVisitDate;
import static CommonUtils.CommonUtils.parseVisitTime;

/**
 * This controller manages the waitlist page where users can sign up for the waitlist when the park is at full capacity.
 * It displays information about the visitation order, including the park name, visitation date, time, visitor's details,
 * and provides a button for users to sign up for the waitlist. The controller handles button clicks to sign up users
 * for the waitlist, retrieves the current user from the application window controller, and processes the order accordingly.
 * It distinguishes between single visitors and visitor group guides and sends appropriate messages to the server based on the user type.
 * The controller displays confirmation popups based on the server response and allows users to return to the dashboard after signing up.
 */

public class WaitListPageController extends BaseController implements Initializable {


    /**
     * Button for signing up.
     */
    @FXML
    private MFXButton btnSignUp;

    /**
     * Label for displaying the visitation date.
     */
    @FXML
    private Label lblDate;

    /**
     * Label for displaying the visitor's email.
     */
    @FXML
    private Label lblEmail;

    /**
     * Label for displaying the visitor's name.
     */
    @FXML
    private Label lblName;

    /**
     * Label for displaying the number of visitors.
     */
    @FXML
    private Label lblNumOfVisitors;

    /**
     * Label for displaying the park's name.
     */
    @FXML
    private Label lblParkName;

    /**
     * Label for displaying the visitor's phone number.
     */
    @FXML
    private Label lblPhone;

    /**
     * Label for displaying the visitation time.
     */
    @FXML
    private Label lblTime;

    /**
     * Text for displaying description.
     */
    @FXML
    private Text txtDescription;

    /**
     * Text for displaying header.
     */
    @FXML
    private Text txtHeader;

    /**
     * Label for displaying any error messages.
     */
    @FXML
    private Label lblError;

    /**
     * The order associated with the UI elements.
     */
    private Order order;


    /**
     * Sets the fields of the UI elements based on the provided order and visitor's full name.
     * Clears any previous error message.
     *
     * @param o1           The order object containing the order details.
     * @param tempfullName The full name of the visitor associated with the order.
     */
    public void setFields(Order o1, String tempfullName) {
        lblError.setText("");
        this.order = o1;
        lblParkName.setText(ParkBank.getParkNameByID(order.getParkID()));
        lblEmail.setText(order.getClientEmailAddress());
        lblPhone.setText(order.getPhoneNumber());
        lblNumOfVisitors.setText(String.valueOf(order.getNumOfVisitors()).toString());
        Timestamp timestamp = order.getVisitationDate();
        String date = parseVisitDate(timestamp);
        String time = parseVisitTime(timestamp);
        lblName.setText(tempfullName);
        lblDate.setText(date);
        lblTime.setText(time);
    }


    /**
     * Handles the action event when the "Sign Up" button is clicked.
     * Retrieves the current user from the application window controller and processes the order accordingly.
     * If the user is a single visitor, sets the order status to waitlist, sends a message to the server, and handles the response.
     * If the user is a visitor group guide, follows similar steps as for single visitors.
     * Shows appropriate confirmation popups based on the server response.
     *
     * @param actionEvent The action event triggered by clicking the button.
     */
    @FXML
    public void OnClickSignUpButton(ActionEvent actionEvent) {
        User user = applicationWindowController.getUser();
        if (user instanceof SingleVisitor) {
            Order temporder = order;
            temporder.setOrderStatus(OrderStatus.STATUS_WAITLIST);
            Object msg = new Message(OpCodes.OP_INSERT_VISITATION_TO_WAITLIST, user.getUsername(), temporder);
            ClientUI.client.accept(msg);
            Message respondMsg = ClientCommunicator.msg;
            OpCodes returnOpCode = respondMsg.getMsgOpcode();
            if (returnOpCode == OpCodes.OP_ORDER_ALREADY_EXIST) {
                ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
                confirmationPopup.show(applicationWindowController.getRoot());
                return;
            }
            if (returnOpCode == OpCodes.OP_DB_ERR) {
                ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
                confirmationPopup.show(applicationWindowController.getRoot());
                return;
            }
            // Checking if the response from the server is inappropriate.
            if (returnOpCode != OpCodes.OP_INSERT_VISITATION_TO_WAITLIST) {
                ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
                confirmationPopup.show(applicationWindowController.getRoot());
                return;
            }
            if (!(respondMsg.getMsgData() instanceof Order)) {
                ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
                confirmationPopup.show(applicationWindowController.getRoot());
                return;
            }
            Order cnfrmorder = (Order) respondMsg.getMsgData();
            String strForPopup = "The order " + cnfrmorder.getOrderID() + " has been created successfully and you entered to wait list  ";
            ConfirmationPopup confirmPopup = new ConfirmationPopup(strForPopup, () ->
            {
                applicationWindowController.loadDashboardPage(applicationWindowController.getUser().getRole());
                applicationWindowController.loadMenu(applicationWindowController.getUser());
            }
                    , 800, 400, false, "OK", false);
            confirmPopup.show(applicationWindowController.getRoot());
        } else if (user instanceof VisitorGroupGuide) {
            Order temporder = order;
            temporder.setOrderStatus(OrderStatus.STATUS_WAITLIST);
            Object msg = new Message(OpCodes.OP_INSERT_VISITATION_TO_WAITLIST, user.getUsername(), order);
            ClientUI.client.accept(msg);
            Message respondMsg = ClientCommunicator.msg;

            OpCodes returnOpCode = respondMsg.getMsgOpcode();
            if (returnOpCode == OpCodes.OP_DB_ERR) {
                ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
                confirmationPopup.show(applicationWindowController.getRoot());
                return;
            }
            // Checking if the response from the server is inappropriate.
            if (returnOpCode != OpCodes.OP_INSERT_VISITATION_TO_WAITLIST) {
                ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
                confirmationPopup.show(applicationWindowController.getRoot());
                return;
            }
            if (!(respondMsg.getMsgData() instanceof Order)) {
                ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
                confirmationPopup.show(applicationWindowController.getRoot());
                return;
            }
            Order cnfrmorder = (Order) respondMsg.getMsgData();
            String strForPopup = "The order " + cnfrmorder.getOrderID() + " has been created successfully and you entered to wait list";
            ConfirmationPopup confirmPopup = new ConfirmationPopup(strForPopup, () ->
            {
                applicationWindowController.loadDashboardPage(applicationWindowController.getUser().getRole());
                applicationWindowController.loadMenu(applicationWindowController.getUser());
            }
                    , 800, 400, false, "OK", false);
            confirmPopup.show(applicationWindowController.getRoot());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
