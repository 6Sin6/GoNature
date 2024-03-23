package VisitorsControllers;

import CommonClient.ClientUI;
import CommonClient.controllers.BaseController;
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
    @FXML
    private Text txtHeader;

    @FXML
    private Text txtDescription;

    @FXML
    private Label lblParkName;

    @FXML
    private Label lblNumOfVisitors;

    @FXML
    private Label lblTelephone;

    @FXML
    private Label lblEmail;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblTime;

    @FXML
    private Label lblOrderNumber;

    @FXML
    private MFXButton btnDeclineVisitation;

    @FXML
    private MFXButton btnConfirmVisitation;

    private Order order;



    private ArrayList<Order> ordersList;

    public void setOrdersList(ArrayList<Order> ordersList) {
        this.ordersList = ordersList;
    }
    public void cleanup() {
        order = null;
    }

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

    @FXML
    void OnClickConfirmVisitationButton(ActionEvent event) {

    }

    @FXML
    void OnClickDeclineVisitationButton(ActionEvent event) throws CommunicationException {
        boolean flag;
        User user = applicationWindowController.getUser();
        Object msg = new Message(OpCodes.OP_HANDLE_VISITATION_CANCEL_ORDER, user.getUsername(), this.order);
        ClientUI.client.accept(msg);
        Message respondMsg = ClientCommunicator.msg;
        if (respondMsg.getMsgOpcode() != OpCodes.OP_HANDLE_VISITATION_CANCEL_ORDER) {
            throw new CommunicationException("Respond not appropriate from server");
        }
        String strForPopup = "The order has been canceled successfully";
        if (ordersList.size() == 1 && order.getOrderType() == OrderType.ORD_TYPE_SINGLE) {
            strForPopup  = "Your only order has been cancelled you are getting redirected to home page";
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
