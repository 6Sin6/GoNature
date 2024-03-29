package VisitorsControllers;

import CommonClient.ClientUI;
import CommonClient.controllers.OrderBillPageController;
import CommonUtils.CommonUtils;
import CommonUtils.ConfirmationPopup;
import CommonUtils.MessagePopup;
import Entities.*;
import client.ClientCommunicator;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Date;

public class GenerateBillForGroupGuideController extends OrderBillPageController {

    /**
     * Reference to the Text node representing the discount in the UI.
     */
    @FXML
    private Text discountTxt;

    /**
     * Reference to the Text node representing the full price in the UI.
     */
    @FXML
    private Text fullPriceTxt;

    /**
     * Reference to the Text node representing the date in the UI.
     */
    @FXML
    private Text dateTxt;

    /**
     * Reference to the Text node representing the number of visitors in the UI.
     */
    @FXML
    private Text numVisitorsTxt;

    /**
     * Reference to the Text node representing the order ID in the UI.
     */
    @FXML
    private Text orderIdTxt;

    /**
     * Reference to the Text node representing the order type in the UI.
     */
    @FXML
    private Text orderTypeTxt;

    /**
     * Reference to the Text node representing the price after discount in the UI.
     */
    @FXML
    private Text priceAfterDiscTxt;

    /**
     * Reference to the Text node representing the type description in the UI.
     */
    @FXML
    private Text typeDescTxt;

    /**
     * Reference to the Text node representing the price in the UI.
     */
    @FXML
    private Text priceTxt;

    /**
     * Reference to the controller for the message popup.
     */
    private MessagePopup messageController;

    /**
     * Flag indicating whether the controller is for a group guide page.
     */
    private boolean groupGuidePage;

    /**
     * The Order object associated with the controller.
     */
    private Order o1;


    /**
     * Cleans up the UI by resetting the text content of various Text nodes to empty strings.
     * This method is typically called to reset the UI state before displaying new data.
     */
    public void cleanup() {
        discountTxt.setText("");
        fullPriceTxt.setText("");
        numVisitorsTxt.setText("");
        orderIdTxt.setText("");
        orderTypeTxt.setText("");
        priceAfterDiscTxt.setText("");
        typeDescTxt.setText("");
    }


    /**
     * Sets the message popup controller associated with this controller.
     *
     * @param messageController The MessagePopup controller to be associated with this controller.
     *                          This controller will use the provided message popup controller for operations such as closing the popup.
     */
    public void setMessagePopup(MessagePopup messageController) {
        this.messageController = messageController;
    }

    /**
     * Closes the message popup without further action.
     * This method is called when the user wants to close the popup without taking any action.
     */
    public void closePopup() {
        messageController.closePopup(false);
    }


    /**
     * Proceeds to the payment process.
     * This method is called when the user clicks on a button to proceed to payment.
     * It closes the current message popup, loads the appropriate page for payment,
     * and handles the payment process for group guide orders.
     */
    public void proceedToPayment() {
        messageController.closePopup(true);
        if (!groupGuidePage) {
            applicationWindowController.loadEmployeesPage("GenerateBillPage");
        } else {
            Message msgPaid = new Message(OpCodes.OP_MARK_GROUP_GUIDE_ORDER_AS_PAID, applicationWindowController.getUser().getUsername(), this.o1);
            ClientUI.client.accept(msgPaid);
            Message response = ClientCommunicator.msg;
            OpCodes returnOpCode = response.getMsgOpcode();
            if (returnOpCode == OpCodes.OP_DB_ERR) {
                ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
                confirmationPopup.show(applicationWindowController.getRoot());
                return;
            }
            // Checking if the response from the server is inappropriate.
            if (returnOpCode != OpCodes.OP_MARK_GROUP_GUIDE_ORDER_AS_PAID) {
                ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
                confirmationPopup.show(applicationWindowController.getRoot());
                return;
            }
            applicationWindowController.loadVisitorsPage("ActiveOrdersPage");
            Object controller = applicationWindowController.getCurrentActiveController();
            if (controller instanceof ActiveOrdersPageController) {
                ((ActiveOrdersPageController) controller).start();
                ((ActiveOrdersPageController) controller).populateTable((ArrayList) (ClientCommunicator.msg.getMsgData()));
            }
        }
    }


    /**
     * Initializes the UI with information related to the provided order.
     *
     * @param order             The order for which the UI should be initialized.
     * @param referredPostOrder Flag indicating whether the UI initialization is for a post-order scenario.
     *                          If true, the UI will be tailored accordingly for post-order processing.
     */
    public void start(Order order, boolean referredPostOrder) {
        this.groupGuidePage = referredPostOrder;
        o1 = order;

        Discount discountType = Discount.getDiscountType(order.getOrderType(), order.getOrderStatus(), true);
        Double fullPrice = discountType !=
                Discount.PREPAID_PREORDERED_GROUP_DISCOUNT ?
                order.getNumOfVisitors() * Order.pricePerVisitor :
                (order.getNumOfVisitors() - 1) * Order.pricePerVisitor;
        fullPriceTxt.setText(String.format("%.2f", fullPrice));


        if (discountType != null) {
            discountTxt.setText(Discount.displayString(discountType));
            priceAfterDiscTxt.setText(String.format("%.2f", Discount.applyDiscount(fullPrice, discountType)));
        }

        Date date = new Date();
        dateTxt.setText(date.toString());
        orderIdTxt.setText(order.getOrderID());
        numVisitorsTxt.setText(String.valueOf(order.getNumOfVisitors()));
        priceTxt.setText(String.valueOf(Order.pricePerVisitor));
        orderTypeTxt.setText(order.getOrderType().toString());

        String orderTypeDescription = "";
        if (order.getOrderType() == OrderType.ORD_TYPE_SINGLE) {
            boolean isFamilySized = order.getNumOfVisitors() > 1;
            orderTypeTxt.setText(isFamilySized ? "Family-sized visitation" : "Single visitation");
            if (order.getOrderStatus() == OrderStatus.STATUS_SPONTANEOUS_ORDER) {
                orderTypeDescription = isFamilySized ? "Spontaneous family-sized visitation" : "Spontaneous single visitation";
            } else {
                orderTypeDescription = isFamilySized ? "Pre-ordered family-sized visitation" : "Pre-ordered single visitation";
            }
        } else if (order.getOrderType() == OrderType.ORD_TYPE_GROUP) {
            orderTypeTxt.setText("Group visitation");
            orderTypeDescription = order.getOrderStatus() == OrderStatus.STATUS_SPONTANEOUS_ORDER ? "Spontaneous group visitation" : "Pre-ordered group visitation";
        }

        typeDescTxt.setText(orderTypeDescription);
    }
}