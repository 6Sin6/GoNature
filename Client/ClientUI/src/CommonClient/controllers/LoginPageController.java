package CommonClient.controllers;

import CommonClient.ClientUI;
import CommonUtils.CommonUtils;
import CommonUtils.ConfirmationPopup;
import CommonUtils.MessagePopup;
import EmployeesControllers.DepartmentManagerDashboardPageController;
import EmployeesControllers.ParkManagerDashboardPageController;
import Entities.*;
import client.ClientCommunicator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import javax.naming.CommunicationException;
import java.util.ArrayList;

/**
 * This controller class manages the login page of a JavaFX application. It is responsible for
 * capturing user input for login credentials, validating these credentials, and facilitating
 * the navigation based on the user's role after successful authentication.
 */
public class LoginPageController extends BaseController {

    /**
     * The label used to display error messages related to the login process.
     */
    @FXML
    private Label ErrorMsg;

    /**
     * A password field for the user to enter their password securely.
     */
    @FXML
    private PasswordField passwordText;

    /**
     * A text field for the user to enter their username.
     */
    @FXML
    private TextField userNameText;

    /**
     * Retrieves the username from the username text field.
     *
     * @return A string containing the username entered by the user.
     */
    private String getUserName() {
        return userNameText.getText();
    }

    /**
     * Retrieves the password from the password field.
     *
     * @return A string containing the password entered by the user.
     */
    private String getPassword() {
        return passwordText.getText();
    }

    /**
     * Resets all input fields and error messages to their default states.
     * This method clears the text from the username and password fields,
     * and resets the error message label.
     */
    public void resetAllFields() {
        this.ErrorMsg.setText("");
        this.userNameText.setText("");
        this.passwordText.setText("");
    }

    /**
     * Cleans up the UI by resetting all fields. This method is typically called
     * during a logout process or when navigating away from the login page to ensure
     * that sensitive information is not left displayed in the UI components.
     */
    public void cleanup() {
        resetAllFields();
    }

    /**
     * Navigates the user interface to the home page. This method is used after
     * a successful login or logout, or when the user needs to be redirected to the
     * home page for any other reason.
     */
    public void navigateToHomePage() {
        applicationWindowController.setCenterPage("/CommonClient/gui/HomePage.fxml");
    }

    /**
     * Handles the action triggered by the login button click. This method validates the input fields to ensure
     * they are not empty, constructs a {@link User} object with the provided username and password, and sends
     * a sign-in request to the server. Depending on the server's response, it either shows an error message,
     * displays a confirmation popup for various errors, or proceeds to log the user in and navigate to the
     * appropriate dashboard page.
     *
     * <p>It checks for multiple conditions such as if the user is already logged in, if there are database
     * errors, server errors, or if the user credentials do not match an activated Visitor Group Guide or
     * a valid user role. For Visitor Group Guides with orders pending confirmation, it fetches and processes
     * these orders specifically.</p>
     *
     * <p>Errors are communicated to the user through the {@code ErrorMsg} text field or through pop-ups
     * for database or server errors. Upon successful login, it resets the login fields and updates the UI
     * to reflect the user's role and possibly displays a popup if there are orders pending confirmation.</p>
     *
     * @throws CommunicationException If there's an issue communicating with the server.
     */
    public void onLoginClick() throws CommunicationException {
        if (getUserName().isEmpty() || getPassword().isEmpty()) {
            ErrorMsg.setText("Please fill all fields!");
            return; // Exit the method if any of the fields are empty.
        }
        User user = new User(getUserName(), getPassword());
        Object msg = new Message(OpCodes.OP_SIGN_IN, getUserName(), user);

        ClientUI.client.accept(msg);
        Message respondMsg = ClientCommunicator.msg;

        OpCodes returnOpCode = respondMsg.getMsgOpcode();
        // Checking if the user is already signed in:
        if (returnOpCode == OpCodes.OP_SIGN_IN_ALREADY_LOGGED_IN) {
            ErrorMsg.setText(respondMsg.getMsgUserName() + " is already logged in.");
            return;
        }
        if (returnOpCode == OpCodes.OP_DB_ERR) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        // Checking if the response from the server is inappropriate.
        if (returnOpCode != OpCodes.OP_SIGN_IN) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        if (respondMsg.getMsgData() instanceof String && (((String) respondMsg.getMsgData()).contains("Visitor Group Guide is not activated"))) {
            ErrorMsg.setText("Visitor Group Guide is not activated");
            return;
        }
        if (respondMsg.getMsgData() == null || !(respondMsg.getMsgData() instanceof User)) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        } else if (((User) respondMsg.getMsgData()).getRole().equals(Role.ROLE_GUEST)) {
            ErrorMsg.setText("Invalid username or password");
            return;
        }


        // Logging user in, unless incorrect user and password.
        user = (User) respondMsg.getMsgData();
        if (respondMsg.getMsgData() != null && user.getRole() != Role.ROLE_GUEST) {
            resetAllFields();
            if (user.getRole() == Role.ROLE_VISITOR_GROUP_GUIDE) {
                Message ordersMessage = new Message(OpCodes.OP_GET_VISITOR_ORDERS, user.getUsername(), user);
                ClientUI.client.accept(ordersMessage);
                Message ordersResponse = ClientCommunicator.msg;
                if (ordersResponse.getMsgOpcode() == OpCodes.OP_DB_ERR) {
                    ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
                    confirmationPopup.show(applicationWindowController.getRoot());
                    return;
                }
                if (ordersResponse.getMsgOpcode() != OpCodes.OP_GET_VISITOR_ORDERS) {
                    ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
                    confirmationPopup.show(applicationWindowController.getRoot());
                    return;
                }
                if (!(ordersResponse.getMsgData() instanceof ArrayList)) {
                    ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
                    confirmationPopup.show(applicationWindowController.getRoot());
                    return;
                }
                ArrayList<Order> orders = (ArrayList<Order>) ordersResponse.getMsgData();

                ArrayList<Order> activeOrders = new ArrayList<>();
                ArrayList<Order> OrdersToConfirm = new ArrayList<>();
                boolean flag = false;
                for (Order order : orders) {
                    if (order.getOrderStatus() != OrderStatus.STATUS_CANCELLED &&
                            order.getOrderStatus() != OrderStatus.STATUS_CONFIRMED_AND_ABSENT &&
                            order.getOrderStatus() != OrderStatus.STATUS_FULFILLED) {
                        activeOrders.add(order);
                    }
                    if (order.getOrderStatus() == OrderStatus.STATUS_PENDING_CONFIRMATION) {
                        flag = true;
                        OrdersToConfirm.add(order);
                    }
                }
                applicationWindowController.loadDashboardPage(user.getRole());
                applicationWindowController.loadMenu(user);
                if (flag) {
                    MessagePopup messagePopup = new MessagePopup("You have " + OrdersToConfirm.size() + " orders pending confirmation", Duration.seconds(5), 500, 300, false);
                    messagePopup.show(applicationWindowController.getRoot());
                }
            } else {
                applicationWindowController.loadDashboardPage(user.getRole());
                applicationWindowController.loadMenu(user);
            }
        } else {
            ErrorMsg.setText("Invalid username or password, Please try again!");
        }
    }
}
