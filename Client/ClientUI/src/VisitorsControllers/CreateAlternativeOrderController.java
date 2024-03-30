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

import java.net.URL;
import java.sql.Timestamp;
import java.util.ResourceBundle;

import static CommonUtils.CommonUtils.parseVisitDate;
import static CommonUtils.CommonUtils.parseVisitTime;

/**
 * This class represents the controller for creating alternative visitation orders.
 * It controls the UI elements related to signing up for visitation, displaying order details,
 * handling user interactions, and communicating with the server to create new visitation orders.
 * The controller sets up labels to display details of the visitation order, such as park name,
 * number of visitors, contact information, visitation date and time, and order number.
 * It also provides a method for setting the fields with order details and a full name.
 * The class includes an event handler for clicking the sign-up button, which sends a message to the server
 * to create a new visitation order and displays appropriate confirmation or error messages based on the server response.
 * Additionally, it handles the initialization of the controller after loading the FXML file
 * and manages the presentation of a bill for the order in case of a visitor group guide.
 * The class assumes the existence of an application window controller for managing page navigation and popups,
 * as well as a client communicator for communicating with the server.
 */

public class CreateAlternativeOrderController extends BaseController implements Initializable {

    /**
     * Button for signing up.
     */
    @FXML
    private MFXButton btnSignUp;

    /**
     * Label for displaying the date.
     */
    @FXML
    private Label lblDate;

    /**
     * Label for displaying the email address.
     */
    @FXML
    private Label lblEmail;

    /**
     * Label for displaying the name.
     */
    @FXML
    private Label lblName;

    /**
     * Label for displaying the number of visitors.
     */
    @FXML
    private Label lblNumOfVisitors;

    /**
     * Label for displaying the park name.
     */
    @FXML
    private Label lblParkName;

    /**
     * Label for displaying the phone number.
     */
    @FXML
    private Label lblPhone;

    /**
     * Label for displaying the time.
     */
    @FXML
    private Label lblTime;

    /**
     * Label for displaying error messages.
     */
    @FXML
    private Label lblError;

    /**
     * The order associated with this controller.
     */
    private Order order;


    /**
     * Sets the fields with the provided order details and full name.
     *
     * @param o1           The order object containing visitation details.
     * @param tempfullName The full name to be displayed.
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
     * Handles the action event triggered by clicking the sign-up button.
     * This method is typically associated with an action event listener in a JavaFX application.
     *
     * @param actionEvent The ActionEvent object generated when the sign-up button is clicked.
     */
    @FXML
    public void OnClickSignUpButton(ActionEvent actionEvent) {
        User user = applicationWindowController.getUser();
        if (user instanceof SingleVisitor) {
            Object msg = new Message(OpCodes.OP_CREATE_NEW_VISITATION, user.getUsername(), order);
            ClientUI.client.accept(msg);

            Message respondMsg = ClientCommunicator.msg;
            OpCodes returnOpCode = respondMsg.getMsgOpcode();
            if (returnOpCode == OpCodes.OP_DB_ERR) {
                ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
                confirmationPopup.show(applicationWindowController.getRoot());
                return;
            }
            // Checking if the response from the server is inappropriate.
            if (returnOpCode != OpCodes.OP_CREATE_NEW_VISITATION && returnOpCode != OpCodes.OP_NO_AVAILABLE_SPOT && returnOpCode != OpCodes.OP_ORDER_ALREADY_EXIST) {
                ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
                confirmationPopup.show(applicationWindowController.getRoot());
                return;
            }
            if (returnOpCode == OpCodes.OP_CREATE_NEW_VISITATION && !(respondMsg.getMsgData() instanceof Order)) {
                ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
                confirmationPopup.show(applicationWindowController.getRoot());
                return;
            }
            if (respondMsg.getMsgOpcode() == OpCodes.OP_CREATE_NEW_VISITATION) {
                Order cnfrmorder = (Order) respondMsg.getMsgData();
                String strForPopup = "The order " + cnfrmorder.getOrderID() + " has been created successfully";
                ConfirmationPopup confirmPopup = new ConfirmationPopup(strForPopup, () ->
                {
                    applicationWindowController.loadDashboardPage(applicationWindowController.getUser().getRole());
                    applicationWindowController.loadMenu(applicationWindowController.getUser());
                }
                        , 600, 300, false, "OK", false);
                confirmPopup.show(applicationWindowController.getRoot());

            } else if (returnOpCode == OpCodes.OP_ORDER_ALREADY_EXIST) {
                String strForPopup = "You already have an order with these details...\nWould you like to view alternatives times?";
                ConfirmationPopup confirmPopup = new ConfirmationPopup(strForPopup, () ->
                {
                    applicationWindowController.loadVisitorsPage("AlternativeTimesTable");
                    Object controller = applicationWindowController.getCurrentActiveController();
                    if (controller instanceof AlternativeTimesTableController) {
                        ((AlternativeTimesTableController) controller).start(order, lblName.getText());
                    }
                }, () ->
                {
                    applicationWindowController.loadDashboardPage(applicationWindowController.getUser().getRole());
                    applicationWindowController.loadMenu(applicationWindowController.getUser());
                }
                        , 600, 300, false, "Alternative Time", "Dashboard", false);
                confirmPopup.show(applicationWindowController.getRoot());
            } else {
                String strForPopup = "This alternative spot has been taken already...\nWould you like to join the wait list or to see alternative times?";
                ConfirmationPopup confirmPopup;
                String fullName = lblName.getText();
                confirmPopup = new ConfirmationPopup(strForPopup, () ->
                {
                    applicationWindowController.loadVisitorsPage("WaitListPage");
                    Object controller = applicationWindowController.getCurrentActiveController();
                    if (controller instanceof WaitListPageController) {
                        ((WaitListPageController) controller).setFields(order, fullName);
                    }

                }, () -> {
                    applicationWindowController.loadVisitorsPage("AlternativeTimesTable");
                    Object controller = applicationWindowController.getCurrentActiveController();
                    if (controller instanceof AlternativeTimesTableController) {
                        ((AlternativeTimesTableController) controller).start(order, fullName);
                    }
                },
                        1100, 500, false, "Waitlist", "Alternative Times", false);
                confirmPopup.show(applicationWindowController.getRoot());
            }
        } else if (user instanceof VisitorGroupGuide) {
            Object msg = new Message(OpCodes.OP_CREATE_NEW_VISITATION, user.getUsername(), order);
            ClientUI.client.accept(msg);
            Message respondMsg = ClientCommunicator.msg;
            OpCodes returnOpCode = respondMsg.getMsgOpcode();
            if (returnOpCode == OpCodes.OP_DB_ERR) {
                ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
                confirmationPopup.show(applicationWindowController.getRoot());
                return;
            }
            if (returnOpCode != OpCodes.OP_CREATE_NEW_VISITATION && returnOpCode != OpCodes.OP_NO_AVAILABLE_SPOT && returnOpCode != OpCodes.OP_ORDER_ALREADY_EXIST) {
                ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
                confirmationPopup.show(applicationWindowController.getRoot());
                return;
            }
            if (returnOpCode != OpCodes.OP_CREATE_NEW_VISITATION && !(respondMsg.getMsgData() instanceof Order)) {
                ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
                confirmationPopup.show(applicationWindowController.getRoot());
                return;
            }
            if (returnOpCode == OpCodes.OP_CREATE_NEW_VISITATION) {
                Order cnfrmorder = (Order) respondMsg.getMsgData();
                String strForPopup = "The order " + cnfrmorder.getOrderID() + " has been created successfully! Would you like to pay now and get discount or pay later ?";
                ConfirmationPopup confirmPopup = new ConfirmationPopup(strForPopup, () -> handleBillPresentation(cnfrmorder), () ->
                {
                    applicationWindowController.loadDashboardPage(applicationWindowController.getUser().getRole());
                    applicationWindowController.loadMenu(applicationWindowController.getUser());
                }
                        , 950, 500, false, "Pay now!", "Later", false);
                confirmPopup.show(applicationWindowController.getRoot());
            } else if (returnOpCode == OpCodes.OP_ORDER_ALREADY_EXIST) {
                String strForPopup = "You already have an order with these details...\nWould you like to view alternatives times?";
                ConfirmationPopup confirmPopup = new ConfirmationPopup(strForPopup, () ->
                {
                    applicationWindowController.loadVisitorsPage("AlternativeTimesTable");
                    Object controller = applicationWindowController.getCurrentActiveController();
                    if (controller instanceof AlternativeTimesTableController) {
                        ((AlternativeTimesTableController) controller).start(order, lblName.getText());
                    }
                }, () ->
                {
                    applicationWindowController.loadDashboardPage(applicationWindowController.getUser().getRole());
                    applicationWindowController.loadMenu(applicationWindowController.getUser());
                }
                        , 600, 300, false, "Alternative Time", "Dashboard", false);
                confirmPopup.show(applicationWindowController.getRoot());

            } else {
                String strForPopup = "The park is at full capacity. Would you like to signup to the waitlist?";
                ConfirmationPopup confirmPopup;
                String fullName = lblName.getText();
                confirmPopup = new ConfirmationPopup(strForPopup, () ->
                {
                    applicationWindowController.loadVisitorsPage("WaitListPage");
                    Object controller = applicationWindowController.getCurrentActiveController();
                    if (controller instanceof WaitListPageController) {
                        ((WaitListPageController) controller).setFields(order, fullName);
                    }
                }, () -> {
                    applicationWindowController.loadVisitorsPage("AlternativeTimesTable");
                    Object controller = applicationWindowController.getCurrentActiveController();
                    if (controller instanceof AlternativeTimesTableController) {
                        ((AlternativeTimesTableController) controller).start(order, fullName);
                    }
                },
                        800, 400, false, "WaitListPage", "AlternativeTimesTable", false);
                confirmPopup.show(applicationWindowController.getRoot());
            }
        }
    }

    /**
     * Initializes the controller after its root element has been completely processed.
     * This method is called automatically by the FXMLLoader when loading the FXML file.
     *
     * @param location  The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resources The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }


    /**
     * Handles the presentation of a bill for the given order.
     * This method displays a message popup containing the bill generation UI.
     * It sets up the controller for generating the bill and starts the process.
     *
     * @param order The order for which the bill needs to be generated.
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
