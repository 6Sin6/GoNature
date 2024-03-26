package EmployeesControllers;

import CommonClient.ClientUI;
import CommonClient.controllers.BaseController;
import CommonClient.controllers.OrderBillPageController;
import CommonUtils.ConfirmationPopup;
import CommonUtils.*;
import CommonUtils.MessagePopup;
import Entities.Message;
import Entities.OpCodes;
import Entities.Order;
import client.ClientCommunicator;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.util.Objects;

import static CommonUtils.CommonUtils.isAllDigits;

public class GenerateBillController extends BaseController {
    @FXML
    private MFXButton btnGenerateBill;

    @FXML
    private Label lblErrorMsg;

    @FXML
    private MFXButton paidBtn;

    @FXML
    private Text successMsg;

    @FXML
    private TextField txtOrderID;

    private Order mostRecentOrder;

    public void cleanup() {
        txtOrderID.clear();
        lblErrorMsg.setText("");
        successMsg.setText("");
    }

    @FXML
    void OnClickMarkOrderAsPaid(ActionEvent event) {
        if (mostRecentOrder == null) {
            lblErrorMsg.setText("Please generate a bill first");
            successMsg.setText("");
            return;
        }

        Message msg = new Message(OpCodes.OP_MARK_ORDER_AS_PAID, applicationWindowController.getUser().getUsername(), mostRecentOrder);
        ClientUI.client.accept(msg);

        Message response = ClientCommunicator.msg;
        OpCodes returnOpCode = response.getMsgOpcode();
        if(returnOpCode == OpCodes.OP_DB_ERR)
        {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        // Checking if the response from the server is inappropriate.
        if (returnOpCode != OpCodes.OP_MARK_ORDER_AS_PAID) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        if(!(response.getMsgData() instanceof Boolean)) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }

        lblErrorMsg.setText("");
        successMsg.setText("Order marked as paid successfully");
    }

    @FXML
    void OnClickGenerateBillButton(ActionEvent event) {
        // Validate input of order ID... (positive numbers only)
        if (txtOrderID.getText().isEmpty() || !isAllDigits(txtOrderID.getText()) || Integer.parseInt(txtOrderID.getText()) <= 0) {
            successMsg.setText("");
            lblErrorMsg.setText("Please enter a valid order ID");
            return;
        }

        Message msg = new Message(OpCodes.OP_GET_ORDER_BY_ID, applicationWindowController.getUser().getUsername(), txtOrderID.getText());
        ClientUI.client.accept(msg);

        Message response = ClientCommunicator.msg;
        OpCodes returnOpCode = response.getMsgOpcode();

        if(returnOpCode == OpCodes.OP_DB_ERR)
        {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        // Checking if the response from the server is inappropriate.
        if (returnOpCode != OpCodes.OP_GET_ORDER_BY_ID) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        if(!(response.getMsgData() instanceof Order)) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }


        mostRecentOrder = (Order) response.getMsgData();
        if (Objects.equals(mostRecentOrder.getOrderID(), "")) {
            lblErrorMsg.setText("Order not found");
            successMsg.setText("");
            return;
        }
        lblErrorMsg.setText("");
        successMsg.setText("Bill generated successfully");
        handleBillPresentation(mostRecentOrder);
    }

    private void handleBillPresentation(Order order) {
        try {
            MessagePopup msg = new MessagePopup("/CommonClient/gui/OrderBillPage.fxml", 0, 0, true, false);
            OrderBillPageController controller = (OrderBillPageController) msg.getController();
            controller.setApplicationWindowController(applicationWindowController);
            msg.show(applicationWindowController.getRoot());

            controller.setMessagePopup(msg);
            controller.start(order, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void GenerateBillSpontaneousOrder(String orderID) {
        txtOrderID.setText(orderID);
        OnClickGenerateBillButton(null);
    }

}
