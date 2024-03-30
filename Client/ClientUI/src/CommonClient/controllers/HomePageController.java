package CommonClient.controllers;

import CommonClient.ClientUI;
import CommonClient.Utils;
import CommonUtils.CommonUtils;
import CommonUtils.ConfirmationPopup;
import CommonUtils.InputTextPopup;
import CommonUtils.MessagePopup;
import Entities.*;
import VisitorsControllers.ConfirmVisitationPageController;
import VisitorsControllers.UpdateOrderDetailsPageController;
import client.ClientCommunicator;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import javax.naming.CommunicationException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class HomePageController extends BaseController implements Initializable {
    /**
     * The button used for booking actions.
     */
    @FXML
    private MFXButton bookBtn;

    /**
     * The central image displayed on the home page.
     */
    @FXML
    private ImageView centerImg;

    /**
     * An additional image displayed on the home page.
     */
    @FXML
    private ImageView img1;

    /**
     * Another additional image displayed on the home page.
     */
    @FXML
    private ImageView img2;

    /**
     * The button used for signing in actions.
     */
    @FXML
    private MFXButton signInBtn;

    /**
     * Popup used for authentication and input actions.
     */
    private InputTextPopup onAuthPopup;

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded. Applies a {@link DropShadow} effect
     * to the images on the home page.
     *
     * @param location  The location used to resolve relative paths for the root object, or null if unknown.
     * @param resources The resources used to localize the root object, or null if not available.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        centerImg.toFront();
        centerImg.setEffect(new DropShadow());
        img1.setEffect(new DropShadow());
        img2.setEffect(new DropShadow());
    }

    /**
     * Cleans up resources or UI states if necessary. Currently, this method does not perform any cleanup.
     */
    public void cleanup() {
        // Nothing to clean up
    }

    /**
     * Handles button click events on the home page. Depending on the user's
     * authentication status, it either logs out the user or redirects them to the dashboard page.
     */
    public void onButtonClicked() {
        if (applicationWindowController.getUser() != null) {
            applicationWindowController.logout();
            return;
        }
        applicationWindowController.loadDashboardPage(Role.ROLE_GUEST);
    }

    /**
     * Handles the authentication with ID and order ID. Validates the input and communicates
     * with the server to retrieve order details or display errors accordingly.
     *
     * @param values The input values from the user, expected to contain the user ID and order ID.
     * @throws CommunicationException If communication with the server fails.
     */
    private void onAuthWithID(String... values) throws CommunicationException {
        if (values == null) {
            onAuthPopup.setErrorLabel("Invalid Inputs! Try again");
            return;
        }
        if (!Utils.isIDValid(values[0])) {
            onAuthPopup.setErrorLabel("Invalid ID Format! Try again");
            return;
        }

        if (!Utils.checkContainsDigitsOnly(values[1])) {
            onAuthPopup.setErrorLabel("Invalid Order ID Format! Try again");
            return;
        }

        String[] data = {values[0], values[1]};
        Message signInReq = new Message(OpCodes.OP_SIGN_IN, values[0], values[0]);
        ClientUI.client.accept(signInReq);
        Message respondToSignIn = ClientCommunicator.msg;
        if (respondToSignIn.getMsgOpcode() == OpCodes.OP_SIGN_IN_VISITOR_GROUP_GUIDE) {
            onAuthPopup.setErrorLabel("Activated Group Guides must connect as users via Login !");
            return;
        }
        if (respondToSignIn.getMsgOpcode() == OpCodes.OP_SIGN_IN_ALREADY_LOGGED_IN) {
            onAuthPopup.setErrorLabel("Already Signed In !");
            return;
        }
        if (respondToSignIn.getMsgOpcode() == OpCodes.OP_DB_ERR) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        // Checking if the response from the server is inappropriate.
        if (respondToSignIn.getMsgOpcode() != OpCodes.OP_SIGN_IN) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        Message message = new Message(OpCodes.OP_GET_USER_ORDERS_BY_USERID_ORDERID, "", data);
        ClientUI.client.accept(message);
        Message response = ClientCommunicator.msg;
        OpCodes returnOpCode = response.getMsgOpcode();
        if (returnOpCode == OpCodes.OP_SIGN_IN_VISITOR_GROUP_GUIDE) {
            onAuthPopup.setErrorLabel("Activated Group Guides must connect as users via Login !");
            return;
        }
        if (returnOpCode == OpCodes.OP_DB_ERR) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        // Checking if the response from the server is inappropriate.
        if (returnOpCode != OpCodes.OP_GET_USER_ORDERS_BY_USERID_ORDERID) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        if (!(response.getMsgData() instanceof Order) && response.getMsgData() != null) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        if (response.getMsgData() == null) {
            onAuthPopup.setErrorLabel("Invalid ID or Link! Try again");
            return;
        }
        Order order = (Order) response.getMsgData();
        onAuthPopup.setErrorLabel("");
        try {
            String pathToPage = order.getOrderStatus() == OrderStatus.STATUS_PENDING_CONFIRMATION ? "/VisitorsUI/ConfirmVisitationPage.fxml" : "/VisitorsUI/UpdateOrderDetailsPage.fxml";
            if (order.getOrderType() == OrderType.ORD_TYPE_GROUP) {
                onAuthPopup.setErrorLabel("Group Orders are not allowed to be updated without sign in !");
                return;
            }
            if (order.getOrderStatus() == OrderStatus.STATUS_CANCELLED ||
                    order.getOrderStatus() == OrderStatus.STATUS_CONFIRMED_AND_ABSENT ||
                    order.getOrderStatus() == OrderStatus.STATUS_FULFILLED) {
                onAuthPopup.setErrorLabel("Order Is Inactive");
                return;
            }
            applicationWindowController.setCenterPage(pathToPage);

            applicationWindowController.loadMenu(new SingleVisitor(values[0]));
            Object controller = applicationWindowController.getCurrentActiveController();
            if (controller instanceof BaseController) {
                ((BaseController) controller).setApplicationWindowController(applicationWindowController);
            }

            if (controller instanceof ConfirmVisitationPageController) {
                ((ConfirmVisitationPageController) controller).setOrder(order);
            } else if (controller instanceof UpdateOrderDetailsPageController) {
                ((UpdateOrderDetailsPageController) controller).setFields(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Invoked when handling existing orders. Triggers a popup for the user to input their ID and
     * order ID, and processes the authentication and order retrieval.
     */
    public void handleExistingOrder() {
        if (applicationWindowController.getUser() != null) {
            applicationWindowController.logout();
        }
        onAuthPopup = new InputTextPopup(new String[]{"Enter ID to Authenticate", "Enter Order ID"}, (String[] inputText) -> {
            try {
                this.onAuthWithID(inputText);
            } catch (CommunicationException e) {
                e.printStackTrace();
            }
        }, 500, 300, true, true, true);
        onAuthPopup.show(applicationWindowController.getRoot());
    }

    /**
     * Processes user authentication based on a given ID. This method validates the ID, attempts to sign in,
     * and retrieves the user's orders based on the ID. Depending on the response from the server and the
     * status of the orders, it navigates to different pages or displays appropriate error messages.
     *
     * @param inputID The ID provided by the user for authentication.
     *                It checks if the ID is valid, if the user is already logged in, or if any other error occurs
     *                during the sign-in process. It then proceeds to fetch the user's orders and navigates accordingly.
     */
    protected void onAuth(String inputID) {
        String strToPrint = "";
        if (!Utils.isIDValid(inputID)) {
            strToPrint = "Invalid ID! Try again";
        }
        if (strToPrint.isEmpty()) {
            onAuthPopup.setErrorLabel(strToPrint);
            Message signInReq = new Message(OpCodes.OP_SIGN_IN, inputID, inputID);
            ClientUI.client.accept(signInReq);
            Message respondToSignIn = ClientCommunicator.msg;
            if (respondToSignIn.getMsgOpcode() == OpCodes.OP_SIGN_IN_VISITOR_GROUP_GUIDE) {
                onAuthPopup.setErrorLabel("Activated Group Guides must connect as users via Login !");
                return;
            }
            if (respondToSignIn.getMsgOpcode() == OpCodes.OP_SIGN_IN_ALREADY_LOGGED_IN) {
                onAuthPopup.setErrorLabel("Already Signed In !");
                return;
            }
            if (respondToSignIn.getMsgOpcode() == OpCodes.OP_DB_ERR) {
                ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
                confirmationPopup.show(applicationWindowController.getRoot());
                return;
            }
            // Checking if the response from the server is inappropriate.
            if (respondToSignIn.getMsgOpcode() != OpCodes.OP_SIGN_IN) {
                ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
                confirmationPopup.show(applicationWindowController.getRoot());
                return;
            }
            SingleVisitor visitor = new SingleVisitor(inputID);
            Message message = new Message(OpCodes.OP_GET_VISITOR_ORDERS, inputID, visitor);
            ClientUI.client.accept(message);
            Message respondMsg = ClientCommunicator.msg;
            if (respondMsg.getMsgOpcode() != OpCodes.OP_GET_VISITOR_ORDERS) {
                onAuthPopup.setErrorLabel("Error getting orders");
                return;
            }
            if (respondMsg.getMsgOpcode() == OpCodes.OP_DB_ERR) {
                ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
                confirmationPopup.show(applicationWindowController.getRoot());
                return;
            }
            // Checking if the response from the server is inappropriate.
            if (respondMsg.getMsgOpcode() != OpCodes.OP_GET_VISITOR_ORDERS) {
                ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
                confirmationPopup.show(applicationWindowController.getRoot());
                return;
            }
            if (!(respondMsg.getMsgData() instanceof ArrayList)) {
                ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
                confirmationPopup.show(applicationWindowController.getRoot());
                return;
            }
            ArrayList<Order> orders = (ArrayList<Order>) respondMsg.getMsgData();
            if (orders.isEmpty()) {
                applicationWindowController.setCenterPageForNewVisitor("/VisitorsUI/VisitorOrderVisitationPage.fxml", new SingleVisitor(inputID), "/CommonClient/gui/LeftBackground.fxml");
            } else {
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
                if (activeOrders.isEmpty()) {
                    applicationWindowController.setCenterPageForNewVisitor("/VisitorsUI/VisitorOrderVisitationPage.fxml", new SingleVisitor(inputID), "/CommonClient/gui/LeftBackground.fxml");
                } else {
                    applicationWindowController.setCenterPage("/VisitorsUI/VisitorDashboardPage.fxml");
                    applicationWindowController.loadMenu(new SingleVisitor(inputID));
                    if (flag) {
                        MessagePopup messagePopup = new MessagePopup("You have " + OrdersToConfirm.size() + " orders pending confirmation", Duration.seconds(5), 500, 300, false);
                        messagePopup.show(applicationWindowController.getRoot());
                    }
                }
            }
        } else {
            onAuthPopup.setErrorLabel(strToPrint);
        }
    }

    /**
     * Handles the action triggered when the book button is clicked by the user. This method initiates
     * an authentication process by displaying an input popup where the user is prompted to enter their ID.
     * Upon entering the ID and submitting it, the {@code onAuth} method is called with the entered ID to
     * process the authentication and potentially proceed with the booking process.
     * <p>
     * The popup is configured with specific dimensions and flags to make it modal, ensuring the user's
     * attention is focused on the authentication step before proceeding.
     */
    public void onBookButtonClicked() {
        onAuthPopup = new InputTextPopup(new String[]{"Enter Your ID Please : "}, (inputText) -> this.onAuth(inputText[0]), 500, 300, true, true, true);
        onAuthPopup.show(applicationWindowController.getRoot());
    }
}
