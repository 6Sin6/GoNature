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
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import javax.naming.CommunicationException;
import java.util.ArrayList;

import static CommonUtils.CommonUtils.parseVisitDate;
import static CommonUtils.CommonUtils.parseVisitTime;

public class ConfirmVisitationPageController extends BaseController {
    /**
     * Text field displaying the header or title for the current screen or section.
     * This could be dynamically updated based on the application's state or user actions.
     */
    @FXML
    private Text txtHeader;

    /**
     * Text field for providing a detailed description or instructions to the user.
     * The content can be dynamically updated to reflect the current context or user actions.
     */
    @FXML
    private Text txtDescription;

    /**
     * Label displaying the name of the park associated with the current reservation or order.
     */
    @FXML
    private Label lblParkName;

    /**
     * Label displaying the number of visitors for the current reservation or order.
     */
    @FXML
    private Label lblNumOfVisitors;

    /**
     * Label displaying the telephone contact information provided for the current reservation or order.
     */
    @FXML
    private Label lblTelephone;

    /**
     * Label displaying the email address provided for the current reservation or order.
     */
    @FXML
    private Label lblEmail;

    /**
     * Label displaying the date of the visitation for the current reservation or order.
     */
    @FXML
    private Label lblDate;

    /**
     * Label displaying the time of the visitation for the current reservation or order.
     */
    @FXML
    private Label lblTime;

    /**
     * Label displaying the unique order number for the current reservation or order.
     * This can be used for tracking and reference purposes.
     */
    @FXML
    private Label lblOrderNumber;

    /**
     * Button to decline the current reservation or visitation request.
     * Triggering this action may require confirmation and will update the order's status accordingly.
     */
    @FXML
    private MFXButton btnDeclineVisitation;

    /**
     * Button to confirm the current reservation or visitation request.
     * Triggering this action will finalize the order and may send notifications to the user.
     */
    @FXML
    private MFXButton btnConfirmVisitation;

    /**
     * Represents the currently selected or active order.
     * This object contains all relevant information about the reservation or visitation.
     */
    private Order order;

    /**
     * A list of all orders managed by the application.
     * This can be used to track and manage multiple reservations or visitations.
     */
    private ArrayList<Order> ordersList;


    /**
     * Sets the list of orders.
     *
     * @param ordersList The list of orders to be set.
     */
    public void setOrdersList(ArrayList<Order> ordersList) {
        this.ordersList = ordersList;
    }


    /**
     * Cleans up the resources by setting the order object to null.
     */
    public void cleanup() {
        order = null;
    }


    /**
     * Sets the order and updates the displayed information accordingly.
     *
     * @param order The order to be set.
     */
    public void setOrder(Order order) {
        this.order = order;
        lblParkName.setText(ParkBank.getParkNameByID(order.getParkID()));
        lblNumOfVisitors.setText(String.valueOf(order.getNumOfVisitors()));
        lblTelephone.setText(order.getPhoneNumber());
        lblEmail.setText(order.getClientEmailAddress());
        lblDate.setText(parseVisitDate(order.getVisitationDate()));
        lblTime.setText(parseVisitTime(order.getVisitationDate()));
        lblOrderNumber.setText(order.getOrderID());
    }


    /**
     * Handles the action event when the Confirm Visitation Button is clicked.
     * Sends a confirmation message to the server regarding the order visitation.
     * Displays appropriate confirmation or error messages based on the server response.
     *
     * @param event The action event triggered by clicking the Confirm Visitation Button.
     */
    @FXML
    void OnClickConfirmVisitationButton(ActionEvent event) {
        User user = applicationWindowController.getUser();
        Object msg = new Message(OpCodes.OP_CONFIRMATION, user.getUsername(), this.order);
        ClientUI.client.accept(msg);
        Message respondMsg = ClientCommunicator.msg;
        OpCodes returnOpCode = respondMsg.getMsgOpcode();
        if (returnOpCode == OpCodes.OP_DB_ERR) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        // Checking if the response from the server is inappropriate.
        if (returnOpCode != OpCodes.OP_CONFIRMATION) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        String strForPopup = "The order has been confirmed successfully";
        ConfirmationPopup confirmPopup = new ConfirmationPopup(strForPopup, () ->
        {
            applicationWindowController.loadDashboardPage(applicationWindowController.getUser().getRole());
            applicationWindowController.loadMenu(applicationWindowController.getUser());
        }
                , 600, 300, false, "OK", false);
        confirmPopup.show(applicationWindowController.getRoot());
    }


    /**
     * Handles the action event when the Decline Visitation Button is clicked.
     * Sends a message to the server to handle the cancellation of the visitation order.
     * Displays appropriate confirmation or error messages based on the server response.
     *
     * @param event The action event triggered by clicking the Decline Visitation Button.
     */
    @FXML
    void OnClickDeclineVisitationButton(ActionEvent event) {
        boolean flag;
        User user = applicationWindowController.getUser();
        Object msg = new Message(OpCodes.OP_HANDLE_VISITATION_CANCEL_ORDER, user.getUsername(), this.order);
        ClientUI.client.accept(msg);
        Message respondMsg = ClientCommunicator.msg;
        OpCodes returnOpCode = respondMsg.getMsgOpcode();
        if (returnOpCode == OpCodes.OP_DB_ERR) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        // Checking if the response from the server is inappropriate.
        if (returnOpCode != OpCodes.OP_HANDLE_VISITATION_CANCEL_ORDER) {
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
}
