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

public class CreateAlternativeOrderController extends BaseController implements Initializable {

    @FXML
    private MFXButton btnSignUp;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblEmail;

    @FXML
    private Label lblName;

    @FXML
    private Label lblNumOfVisitors;

    @FXML
    private Label lblParkName;

    @FXML
    private Label lblPhone;


    @FXML
    private Label lblTime;

    @FXML
    private Label lblError;

    private Order order;


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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

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
