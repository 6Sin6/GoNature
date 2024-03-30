package VisitorsControllers;

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
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javax.naming.CommunicationException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static CommonUtils.CommonUtils.parseVisitDate;
import static CommonUtils.CommonUtils.parseVisitTime;

/**
 * This controller manages the UI for updating order details, including email, phone number, and cancellation.
 * It allows users to update their order information and handles the communication with the server for updating orders.
 * The controller provides methods to set fields with order details, validate input fields, handle order updates,
 * cancellation, and bill presentation. It also initializes the UI components and processes user interactions.
 * The UI elements include labels, text fields, and buttons for updating, canceling, and paying orders.
 * Additionally, it implements the Initializable interface to initialize the controller class after the FXML file has been loaded.
 */

public class UpdateOrderDetailsPageController extends BaseController implements Initializable {

    /**
     * Label to display the date related to an order or event.
     */
    @FXML
    private Label DateLabel;

    /**
     * Text field for inputting or displaying an email address associated with the order.
     */
    @FXML
    private TextField EmailText;

    /**
     * Label to display the Order ID, uniquely identifying each order.
     */
    @FXML
    private Label OrderIDText;

    /**
     * Label to display the name of the park or location associated with the order.
     */
    @FXML
    private Label ParkName;

    /**
     * Text field for inputting or displaying the phone number associated with the order.
     */
    @FXML
    private TextField PhoneText;

    /**
     * Label to display the time related to an order or event.
     */
    @FXML
    private Label TimeLabel;

    /**
     * Label used for displaying error messages related to UI input or order processing.
     */
    @FXML
    private Label errorLabel;

    /**
     * A button in the UI that, when clicked, triggers the payment process for an order.
     */
    @FXML
    private MFXButton PayBtn;

    /**
     * A list to store multiple orders, potentially for processing or display in the UI.
     */
    private ArrayList<Order> ordersList;

    /**
     * Represents a single order, potentially the current one being processed or viewed.
     */
    private Order order;


    /**
     * Sets the fields of the ConfirmVisitationPageController with the details of the provided order.
     * This method updates the UI elements with the details of the given order, such as order ID, park name, email, phone number,
     * visitation date, and visitation time. It also adjusts the visibility of the "Pay" button based on the order type and status.
     *
     * @param o1 The Order object containing the details to be displayed on the ConfirmVisitationPage.
     */

    public void setFields(Order o1) {
        if (o1.getOrderType() == OrderType.ORD_TYPE_SINGLE || o1.getOrderStatus() == OrderStatus.STATUS_CONFIRMED_PAID || o1.getOrderStatus() == OrderStatus.STATUS_WAITLIST) {
            PayBtn.setVisible(false);
        } else {
            PayBtn.setVisible(true);
        }
        errorLabel.setText("");
        this.order = o1;
        OrderIDText.setText(String.valueOf(order.getOrderID()));
        ParkName.setText(ParkBank.getParkNameByID(order.getParkID()));
        EmailText.setText(order.getClientEmailAddress());
        PhoneText.setText(order.getPhoneNumber());
        Timestamp timestamp = order.getVisitationDate();
        String date = parseVisitDate(timestamp);
        String time = parseVisitTime(timestamp);
        PhoneText.setText(order.getPhoneNumber());
        EmailText.setText(order.getClientEmailAddress());
        DateLabel.setText(date);
        TimeLabel.setText(time);
    }

    public void setOrdersList(ArrayList<Order> ordersList) {
        this.ordersList = ordersList;
    }


    /**
     * Handles the action event when the "Update" button is clicked.
     * This method validates the input fields for email and phone number.
     * If the fields are valid, it constructs a message containing the updated order details
     * and sends it to the server to update the order information in the database.
     * It then processes the response from the server to display appropriate confirmation messages.
     * If the update is successful, it displays a confirmation popup indicating the success.
     * If there is an error during the update process, it displays an error popup.
     *
     * @param actionEvent The ActionEvent representing the user's click on the "Update" button.
     * @throws CommunicationException If there is an issue with communication during the update process.
     */
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
            if (respondMsg.getMsgOpcode() == OpCodes.OP_DB_ERR) {
                ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
                confirmationPopup.show(applicationWindowController.getRoot());
                return;
            }
            if (respondMsg.getMsgOpcode() != OpCodes.OP_UPDATE_ORDER_DETAILS_BY_ORDERID) {
                ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
                confirmationPopup.show(applicationWindowController.getRoot());
                return;
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
            Object msg = new Message(OpCodes.OP_UPDATE_ORDER_DETAILS_BY_ORDERID, user.getUsername(), arrForMsg);
            ClientUI.client.accept(msg);
            Message respondMsg = ClientCommunicator.msg;
            if (respondMsg.getMsgOpcode() == OpCodes.OP_DB_ERR) {
                ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
                confirmationPopup.show(applicationWindowController.getRoot());
                return;
            }
            if (respondMsg.getMsgOpcode() != OpCodes.OP_UPDATE_ORDER_DETAILS_BY_ORDERID) {
                ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
                confirmationPopup.show(applicationWindowController.getRoot());
                return;
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


    /**
     * Validates the input fields for email and phone number.
     * This method checks if the email and phone number fields are empty and if they contain valid values.
     * If any field is empty or contains an invalid value, it sets an appropriate error message on the errorLabel
     * and returns false indicating validation failure.
     * If all fields are filled and contain valid values, it returns true indicating validation success.
     *
     * @return True if all fields are filled and contain valid values, false otherwise.
     */
    private boolean validateFields() {
        if (EmailText.getText().isEmpty() || PhoneText.getText().isEmpty()) {
            errorLabel.setText("Please fill all the fields");
            return false;
        }
        if (!CommonUtils.isEmailAddressValid(EmailText.getText())) {
            errorLabel.setText("Invalid Email address");
            return false;
        }
        if (!CommonUtils.isValidPhone((PhoneText.getText()))) {
            errorLabel.setText("Invalid Phone number");
            return false;
        }
        return true;
    }


    /**
     * Handles the action event when the "Cancel Order" button is clicked.
     * This method initiates the cancellation of the current order by sending a message to the server.
     * It then processes the response from the server to display appropriate confirmation messages.
     * If the cancellation is successful, it displays a confirmation popup indicating the success.
     * If there is an error during the cancellation process, it displays an error popup.
     * If the canceled order is the only order and it's a single visitation order, the method redirects the user to the home page.
     *
     * @param actionEvent The ActionEvent representing the user's click on the "Cancel Order" button.
     */
    public void OnClickCancelOrderBtn(ActionEvent actionEvent) {
        boolean flag;
        User user = applicationWindowController.getUser();
        Object msg = new Message(OpCodes.OP_HANDLE_VISITATION_CANCEL_ORDER, user.getUsername(), this.order);
        ClientUI.client.accept(msg);
        Message respondMsg = ClientCommunicator.msg;
        if (respondMsg.getMsgOpcode() == OpCodes.OP_DB_ERR) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        if (respondMsg.getMsgOpcode() != OpCodes.OP_HANDLE_VISITATION_CANCEL_ORDER) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        String strForPopup = "The order has been canceled successfully";
        if (ordersList.size() == 1 && order.getOrderType() == OrderType.ORD_TYPE_SINGLE) {
            strForPopup = "Your only order has been cancelled you are getting redirected to home page";
            flag = true;
        } else {
            flag = false;
        }
        ConfirmationPopup confirmPopup = new ConfirmationPopup(strForPopup, () ->
        {
            if (flag) {
                applicationWindowController.logout();
                return;
            }
            applicationWindowController.loadDashboardPage(applicationWindowController.getUser().getRole());
            applicationWindowController.loadMenu(applicationWindowController.getUser());
        }
                , 600, 300, false, "OK", false);
        confirmPopup.show(applicationWindowController.getRoot());
    }


    /**
     * Handles the action event when the "Pay Order" button is clicked.
     * This method initiates the presentation of the bill for the current order
     * by calling the handleBillPresentation method with the current order.
     *
     * @param actionEvent The ActionEvent representing the user's click on the "Pay Order" button.
     */
    public void OnClickPayOrderBtn(ActionEvent actionEvent) {
        handleBillPresentation(this.order);
    }


    /**
     * Handles the presentation of the bill for a group guide's order.
     * This method creates a message popup to display the bill using the GenerateBillForGroupGuide.fxml layout.
     * It retrieves the controller associated with the popup, sets the applicationWindowController,
     * and starts the presentation by invoking the start method of the controller with the provided order.
     * If an exception occurs during the process, it prints the stack trace.
     *
     * @param order The order for which the bill is to be presented.
     */
    private void handleBillPresentation(Order order) {
        try {
            MessagePopup msg = new MessagePopup("/VisitorsUI/GenerateBillForGroupGuide.fxml", 0, 0, true, false);
            GenerateBillForGroupGuideController controller = (GenerateBillForGroupGuideController) msg.getController();
            controller.setApplicationWindowController(applicationWindowController);
            msg.show(applicationWindowController.getRoot());

            controller.setMessagePopup(msg);
            controller.start(order, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}