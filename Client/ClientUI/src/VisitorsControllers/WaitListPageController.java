package VisitorsControllers;

import CommonClient.ClientUI;
import CommonClient.controllers.BaseController;
import CommonUtils.ConfirmationPopup;
import Entities.*;
import client.ClientCommunicator;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import javax.naming.CommunicationException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ResourceBundle;

import static CommonUtils.CommonUtils.parseVisitDate;
import static CommonUtils.CommonUtils.parseVisitTime;

public class WaitListPageController extends BaseController implements Initializable {


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
    private Text txtDescription;

    @FXML
    private Text txtHeader;


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
    public void OnClickSignUpButton(ActionEvent actionEvent) throws CommunicationException {
        User user = applicationWindowController.getUser();
        if (user instanceof SingleVisitor) {
            Order temporder = order;
            temporder.setOrderStatus(OrderStatus.STATUS_WAITLIST);
            Object msg = new Message(OpCodes.OP_INSERT_VISITATION_TO_WAITLIST, user.getUsername(), temporder);
            ClientUI.client.accept(msg);
            Message respondMsg = ClientCommunicator.msg;
            if (respondMsg.getMsgOpcode() != OpCodes.OP_INSERT_VISITATION_TO_WAITLIST) {
                throw new CommunicationException("Respond not appropriate from server");
            }
            Order cnfrmorder = (Order) respondMsg.getMsgData();
            String strForPopup = "The order " + cnfrmorder.getOrderID() + " has been created successfully";
            ConfirmationPopup confirmPopup = new ConfirmationPopup(strForPopup, () ->
            {
                applicationWindowController.loadDashboardPage(applicationWindowController.getUser().getRole());
                applicationWindowController.loadMenu(applicationWindowController.getUser());
            }
                    , 600, 300, false, "OK", false);
            confirmPopup.show(applicationWindowController.getRoot());
        } else if (user instanceof VisitorGroupGuide) {
            Order temporder = order;
            temporder.setOrderStatus(OrderStatus.STATUS_WAITLIST);
            Object msg = new Message(OpCodes.OP_INSERT_VISITATION_TO_WAITLIST, user.getUsername(), order);
            ClientUI.client.accept(msg);
            Message respondMsg = ClientCommunicator.msg;
            if (respondMsg.getMsgOpcode() != OpCodes.OP_INSERT_VISITATION_TO_WAITLIST) {
                throw new CommunicationException("Respond not appropriate from server");
            }
            Order cnfrmorder = (Order) respondMsg.getMsgData();
            String strForPopup = "The order " + cnfrmorder.getOrderID() + " has been created successfully";
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
}
