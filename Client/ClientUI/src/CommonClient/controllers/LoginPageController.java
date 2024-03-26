package CommonClient.controllers;

import CommonClient.ClientUI;
import CommonUtils.ConfirmationPopup;
import CommonUtils.MessagePopup;
import CommonUtils.*;
import Entities.*;
import client.ClientCommunicator;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import javax.naming.CommunicationException;
import java.util.ArrayList;

public class LoginPageController extends BaseController {


    @FXML
    private Label ErrorMsg;

    @FXML
    private PasswordField passwordText;

    @FXML
    private TextField userNameText;


    private String getUserName() {
        return userNameText.getText();
    }

    private String getPassword() {
        return passwordText.getText();
    }

    public void resetAllFields() {
        this.ErrorMsg.setText("");
        this.userNameText.setText("");
        this.passwordText.setText("");
    }

    public void cleanup() {
        resetAllFields();
    }

    public void navigateToHomePage() {
        applicationWindowController.setCenterPage("/CommonClient/gui/HomePage.fxml");
    }

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
        if ((!(respondMsg.getMsgData() instanceof User))) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
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
                if (flag) {
                    MessagePopup messagePopup = new MessagePopup("You have " + OrdersToConfirm.size() + " orders pending confirmation", Duration.seconds(5), 500, 300, false);
                    applicationWindowController.loadDashboardPage(user.getRole());
                    applicationWindowController.loadMenu(user);
                    messagePopup.show(applicationWindowController.getRoot());
                }
            } else {

                applicationWindowController.loadDashboardPage(user.getRole());
                applicationWindowController.loadMenu(user);
            }
        } else {
            ErrorMsg.setText("Wrong username or password, Please try again!");
        }
    }
}
