package EmployeesControllers;

import CommonClient.ClientUI;
import CommonClient.controllers.BaseController;
import CommonClient.controllers.OrderBillPageController;
import CommonUtils.CommonUtils;
import CommonUtils.ConfirmationPopup;
import CommonUtils.MessagePopup;
import Entities.Message;
import Entities.OpCodes;
import Entities.Order;
import Entities.OrderType;
import client.ClientCommunicator;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Objects;

import static CommonUtils.CommonUtils.isAllDigits;

public class GenerateBillController extends BaseController {
    /**
     * The button used to generate a bill.
     */
    @FXML
    private MFXButton btnGenerateBill;

    /**
     * A label to display error messages.
     */
    @FXML
    private Label lblErrorMsg;

    /**
     * The button used to enter a park.
     */
    @FXML
    private MFXButton EnterParkButton;

    /**
     * A text element to display success messages.
     */
    @FXML
    private Text successMsg;

    /**
     * A text field for entering an order ID.
     */
    @FXML
    private TextField txtOrderID;

    /**
     * Represents the most recent order.
     */
    private Order mostRecentOrder;


    /**
     * Performs cleanup tasks by resetting various GUI components to their default states.
     * This method clears the text in the order ID text field, resets error messages displayed in the error label,
     * clears success messages displayed in the success text element, and updates the state of two buttons:
     * - Disables the "EnterParkButton".
     * - Enables the "btnGenerateBill".
     */
    public void cleanup() {
        txtOrderID.clear();
        lblErrorMsg.setText("");
        successMsg.setText("");
        EnterParkButton.setDisable(true);
        btnGenerateBill.setDisable(false);
    }


    /**
     * Handles the action event triggered by clicking the "EnterParkButton".
     * This method sends a message to the server to enter visitors to the park based on the most recent order.
     * If there is no recent order, it displays an error message.
     * Upon receiving a response from the server, it handles various scenarios:
     * - If there is a database error, it displays an appropriate error message.
     * - If there is an inappropriate response from the server, it displays a generic server error message.
     * - If the operation is successful, it displays a success message.
     * - If the operation fails, it displays an error message.
     * This method utilizes confirmation and message popups for displaying messages to the user.
     * After handling the response, it calls the 'cleanup' method to reset GUI components to their default states.
     *
     * @param event The ActionEvent triggered by clicking the "EnterParkButton".
     */
    @FXML
    void OnClickEnterParkButton(ActionEvent event) {
        if (mostRecentOrder == null) {
            lblErrorMsg.setText("Please generate a bill first");
            successMsg.setText("");
            return;
        }

        Message msg = new Message(OpCodes.OP_ENTER_VISITORS_TO_PARK, applicationWindowController.getUser().getUsername(), mostRecentOrder);
        ClientUI.client.accept(msg);

        Message response = ClientCommunicator.msg;
        OpCodes returnOpCode = response.getMsgOpcode();
        if (returnOpCode == OpCodes.OP_DB_ERR) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        // Checking if the response from the server is inappropriate.
        if (returnOpCode != OpCodes.OP_ENTER_VISITORS_TO_PARK) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        if (!(response.getMsgData() instanceof Boolean)) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        String msgToPrompt;
        if (!(Boolean) response.getMsgData()) {
            msgToPrompt = "Something went wrong. Please try again.";
        } else {
            msgToPrompt = "Visitors written in the park successfully";
        }
        MessagePopup popup = new MessagePopup(msgToPrompt, Duration.seconds(5), 350, 200, false);
        popup.show(applicationWindowController.getRoot());
        cleanup();
    }


    /**
     * Handles the action event triggered by clicking the "GenerateBillButton".
     * This method validates the input order ID, retrieves the order details from the server, and processes the response.
     * It handles various scenarios based on the order status:
     * - Displays appropriate error messages if the order ID is invalid, not found, or if there are database/server errors.
     * - Disables the "EnterParkButton" and displays relevant messages if the order status is not valid for generating a bill or entering the park.
     * - Handles specific cases for different order statuses, enabling or disabling the "EnterParkButton" accordingly.
     * - Calls the 'handleBillPresentation' method to present the bill if the order status allows.
     * This method utilizes confirmation and message popups for displaying messages to the user.
     */

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

        if (returnOpCode == OpCodes.OP_DB_ERR) {
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
        if (!(response.getMsgData() instanceof Order)) {
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
        successMsg.setText("");
        switch (mostRecentOrder.getOrderStatus()) {
            case STATUS_WAITLIST:
            case STATUS_FULFILLED:
            case STATUS_PENDING_CONFIRMATION:
            case STATUS_CANCELLED:
            case STATUS_CONFIRMED_AND_ABSENT:
                lblErrorMsg.setText("Order status is not valid for generating a bill and entering the park");
                EnterParkButton.setDisable(true);
                break;
            case STATUS_ACCEPTED:
                String propmtMsg;
                if (mostRecentOrder.getOrderType() == OrderType.ORD_TYPE_GROUP) {
                    propmtMsg = "The order can be paid from the group guide user only.";
                } else {
                    propmtMsg = "Order status is not valid for generating a bill and entering the park";
                }
                lblErrorMsg.setText(propmtMsg);
                EnterParkButton.setDisable(true);
                break;
            case STATUS_CONFIRMED_PENDING_PAYMENT:
            case STATUS_SPONTANEOUS_ORDER_PENDING_PAYMENT:
                if (CommonUtils.isTimeBetween(mostRecentOrder.getEnteredTime(), mostRecentOrder.getExitedTime())) {
                    handleBillPresentation(mostRecentOrder);
                } else {
                    EnterParkButton.setDisable(true);
                    lblErrorMsg.setText("Reservation time is not now.");
                }
                break;
            case STATUS_SPONTANEOUS_ORDER:
                if (mostRecentOrder.getVisitorID().equals("SPONTANEOUS_IN_PARK")) {
                    EnterParkButton.setDisable(true);
                    lblErrorMsg.setText("Order has been already redeemed.");
                } else {
                    if (CommonUtils.isTimeBetween(mostRecentOrder.getEnteredTime(), mostRecentOrder.getExitedTime())) {
                        EnterParkButton.setDisable(false);
                        btnGenerateBill.setDisable(true);
                        lblErrorMsg.setText("Order is already paid and the visitor/s can enter the park.");
                    } else {
                        EnterParkButton.setDisable(true);
                        lblErrorMsg.setText("Order is already paid and but the reservation time is not now.");
                    }
                }
                break;
            case STATUS_CONFIRMED_PAID:
                if (CommonUtils.isTimeBetween(mostRecentOrder.getEnteredTime(), mostRecentOrder.getExitedTime())) {
                    EnterParkButton.setDisable(false);
                    btnGenerateBill.setDisable(true);
                    lblErrorMsg.setText("Order is already paid and the visitor/s can enter the park.");
                } else {
                    EnterParkButton.setDisable(true);
                    lblErrorMsg.setText("Order is already paid and but the reservation time is not now.");
                }
                break;

        }
    }

    /**
     * Handles the presentation of the bill for the given order.
     * This method opens a message popup to display the order bill page, passing the order details to the controller.
     * If an exception occurs during the process, it displays a generic server error message.
     *
     * @param order The Order for which the bill is to be presented.
     */
    private void handleBillPresentation(Order order) {
        try {
            MessagePopup msg = new MessagePopup("/CommonClient/gui/OrderBillPage.fxml", 0, 0, true, false);
            OrderBillPageController controller = (OrderBillPageController) msg.getController();
            controller.setApplicationWindowController(applicationWindowController);
            msg.show(applicationWindowController.getRoot());

            controller.setMessagePopup(msg);
            controller.start(order, false);
        } catch (Exception e) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
        }
    }

    /**
     * Generates a bill for a spontaneous order identified by the given order ID.
     * This method sets the order ID in the order ID text field, simulating a click on the "GenerateBillButton",
     * which triggers the generation of the bill for the corresponding order.
     *
     * @param orderID The ID of the spontaneous order for which the bill is to be generated.
     */
    public void GenerateBillSpontaneousOrder(String orderID) {
        txtOrderID.setText(orderID);
        OnClickGenerateBillButton(null);
    }

    /**
     * Sets the order number in the order ID text field.
     *
     * @param orderID The ID of the order to be set in the order ID text field.
     */
    public void setOrderNum(String orderID) {
        txtOrderID.setText(orderID);
    }


    /**
     * Displays a popup message after bill generation indicating the status of the process.
     * This method sets the order number, prepares a message prompt based on whether the bill generation was successful,
     * and displays a message popup with the appropriate message.
     * It also adjusts the state of the "EnterParkButton" and "btnGenerateBill" based on the bill generation status.
     *
     * @param isBillGenerated A boolean indicating whether the bill generation was successful.
     * @param orderID         The ID of the order for which the bill was generated.
     */
    public void showPopUpAfterBillGeneration(boolean isBillGenerated, String orderID) {
        setOrderNum(orderID);
        String msgToPrompt;
        if (isBillGenerated) {
            msgToPrompt = "Order marked as paid successfully";
        } else {
            msgToPrompt = "Something went wrong. Please try again.";
        }
        MessagePopup popup = new MessagePopup(msgToPrompt, Duration.seconds(5), 350, 200, false);
        popup.show(applicationWindowController.getRoot());
        EnterParkButton.setDisable(!isBillGenerated);
        btnGenerateBill.setDisable(isBillGenerated);
    }

}
