package CommonClient.controllers;

import CommonClient.ClientUI;
import CommonClient.Utils;
import CommonUtils.InputTextPopup;
import Entities.*;
import VisitorsControllers.ConfirmVisitationPageController;
import VisitorsControllers.HandleOrderDetailsPageController;
import client.ClientCommunicator;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;

import javax.naming.CommunicationException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class HomePageController extends BaseController implements Initializable {
    @FXML
    private MFXButton bookBtn;

    @FXML
    private ImageView centerImg;

    @FXML
    private ImageView img1;

    @FXML
    private ImageView img2;

    @FXML
    private MFXButton signInBtn;

    private InputTextPopup onAuthPopup;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        centerImg.toFront();
        centerImg.setEffect(new DropShadow());
        img1.setEffect(new DropShadow());
        img2.setEffect(new DropShadow());
    }

    public void cleanup() {
        // Nothing to clean up
    }

    public void onButtonClicked() {
        if (applicationWindowController.getUser() != null) {
            applicationWindowController.logout();
            return;
        }
        applicationWindowController.loadDashboardPage(Role.ROLE_GUEST);
    }

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
        Message message = new Message(OpCodes.OP_GET_USER_ORDERS_BY_USERID_ORDERID, "", data);
        ClientUI.client.accept(message);
        Message response = ClientCommunicator.msg;
        OpCodes returnOpCode = response.getMsgOpcode();

        // Checking if the response from the server is inappropriate.
        if (returnOpCode != OpCodes.OP_GET_USER_ORDERS_BY_USERID_ORDERID) {
            throw new CommunicationException("Response is inappropriate from server");
        }

        Order order = (Order) response.getMsgData();
        if (order == null) {
            onAuthPopup.setErrorLabel("Invalid ID or Link! Try again");
            return;
        }

        onAuthPopup.setErrorLabel("");
        try {
            String pathToPage = order.getOrderStatus() == OrderStatus.STATUS_PENDING_CONFIRMATION ? "/VisitorsUI/ConfirmVisitationPage.fxml" : "/VisitorsUI/HandleOrderDetailsPage.fxml";
            applicationWindowController.setCenterPage(pathToPage);
            Object controller = applicationWindowController.getCurrentActiveController();
            if (controller instanceof BaseController) {
                ((BaseController) controller).setApplicationWindowController(applicationWindowController);
            }

            if (controller instanceof ConfirmVisitationPageController) {
                ((ConfirmVisitationPageController) controller).setOrder(order);
            } else if (controller instanceof HandleOrderDetailsPageController) {
                ((HandleOrderDetailsPageController) controller).setOrder(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
            if (respondToSignIn.getMsgOpcode() != OpCodes.OP_SIGN_IN) {
                onAuthPopup.setErrorLabel("Already Signed In !");
                return;
            }
            SingleVisitor visitor = new SingleVisitor(inputID);
            Message message = new Message(OpCodes.OP_GET_VISITOR_ORDERS, inputID, visitor);
            ClientUI.client.accept(message);
            Message respondMsg = ClientCommunicator.msg;
            ArrayList<Order> orders = (ArrayList<Order>) respondMsg.getMsgData();
            if (orders.isEmpty()) {
                applicationWindowController.setCenterPageForNewVisitor("/VisitorsUI/VisitorOrderVisitationPage.fxml", new SingleVisitor(inputID));
            } else {
                applicationWindowController.setCenterPage("/VisitorsUI/VisitorDashboardPage.fxml");
                applicationWindowController.loadMenu(new SingleVisitor(inputID));
            }

        } else {
            onAuthPopup.setErrorLabel(strToPrint);
        }
    }

    public void onBookButtonClicked() {
        onAuthPopup = new InputTextPopup(new String[]{"Enter Your ID Please : "}, (inputText) -> this.onAuth(inputText[0]), 500, 300, true, false, true);
        onAuthPopup.show(applicationWindowController.getRoot());
    }
}
