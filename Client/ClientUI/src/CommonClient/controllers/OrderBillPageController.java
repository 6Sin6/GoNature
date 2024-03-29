package CommonClient.controllers;

import CommonClient.ClientUI;
import CommonUtils.CommonUtils;
import CommonUtils.ConfirmationPopup;
import CommonUtils.MessagePopup;
import EmployeesControllers.GenerateBillController;
import Entities.*;
import VisitorsControllers.ActiveOrdersPageController;
import client.ClientCommunicator;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Date;

public class OrderBillPageController extends BaseController {
    /**
     * An ImageView serving as a back button, allowing users to return to the previous screen.
     */
    @FXML
    private ImageView backBtn;

    /**
     * A Pane used to display the billing information of an order.
     */
    @FXML
    private Pane billPane;

    /**
     * A Text field displaying the discount applied to the order, if any.
     */
    @FXML
    private Text discountTxt;

    /**
     * A Text field showing the full price of the order before any discounts are applied.
     */
    @FXML
    private Text fullPriceTxt;

    /**
     * A Text field displaying the date of the order.
     */
    @FXML
    private Text dateTxt;

    /**
     * A Text field showing the number of visitors associated with the order.
     */
    @FXML
    private Text numVisitorsTxt;

    /**
     * A Text field displaying the order ID.
     */
    @FXML
    private Text orderIdTxt;

    /**
     * A Text field showing the type of the order (e.g., individual, group, etc.).
     */
    @FXML
    private Text orderTypeTxt;

    /**
     * A Text field displaying the price of the order after any discounts have been applied.
     */
    @FXML
    private Text priceAfterDiscTxt;

    /**
     * A MaterialFX button that users click to proceed with the order or the next step in the process.
     */
    @FXML
    private MFXButton proceedBtn;

    /**
     * A Text field describing the type of visit or order in more detail.
     */
    @FXML
    private Text typeDescTxt;

    /**
     * A Text field displaying the price of something within the order, potentially before discounts.
     */
    @FXML
    private Text priceTxt;

    /**
     * Controller for displaying popup messages to the user.
     */
    private MessagePopup messageController;

    /**
     * A flag indicating whether the current page is intended for group guides.
     */
    private boolean groupGuidePage;

    /**
     * The ID of the order currently being processed or displayed.
     */
    private String orderID;

    /**
     * The most recent order retrieved or processed by the controller.
     */
    private Order mostRecentOrder;

    /**
     * Resets the text fields related to the order details within the UI. This method clears
     * the content of all order-related Text fields, preparing the interface for a new order
     * display or for the user to exit the current view without leaving sensitive information
     * on the screen.
     * <p>
     * This includes clearing information about the order's discount, full price, number of visitors,
     * order ID, order type, price after discount, and the description of the visit type. It is typically
     * called as part of a broader cleanup process, such as when navigating away from the current order
     * view or after finalizing an order process.
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
     * Sets the {@link MessagePopup} controller for this class. This method allows the assignment of an external
     * {@code MessagePopup} instance to the current class, enabling the display of popup messages. The provided
     * {@code MessagePopup} instance can be used for showing informative, warning, or error messages to the user,
     * enhancing the application's interactivity and user feedback mechanisms.
     *
     * @param messageController The {@code MessagePopup} instance to be used for displaying messages.
     */
    public void setMessagePopup(MessagePopup messageController) {
        this.messageController = messageController;
    }

    /**
     * Closes the currently displayed popup message. This method invokes the {@code closePopup} method on the
     * {@link MessagePopup} instance associated with this class, effectively hiding the popup from the user's view.
     * The method is designed to offer a programmable way to dismiss popups, enhancing the user experience by
     * removing unnecessary or action-completed messages from the screen.
     * <p>
     * The boolean parameter passed to the {@code closePopup} method of the {@code MessagePopup} instance is set
     * to {@code false}, indicating that the popup should be closed without any additional conditions or effects.
     */
    public void closePopup() {
        messageController.closePopup(false);
    }


    /**
     * Initiates the payment process for the most recent order. This method first attempts to close any
     * open popups via the {@code messageController}, signaling readiness to proceed with payment. It then
     * sends a message to mark the order as paid if the current page is not dedicated to group guides.
     * <p>
     * For non-group guide pages, it sends an {@link OpCodes#OP_MARK_ORDER_AS_PAID} message with the
     * current user's username and the most recent order details. It handles server responses, including
     * database and server errors, by displaying appropriate confirmation popups. Upon successful marking
     * of the order as paid, it navigates to the "GenerateBillPage" and passes the payment confirmation
     * status to the {@link GenerateBillController}.
     * <p>
     * If the current page is for group guides, it navigates to the "ActiveOrdersPage" and refreshes
     * the orders display by calling the {@code start} and {@code populateTable} methods on the
     * {@link ActiveOrdersPageController}.
     * <p>
     * This method differentiates the logic based on the {@code groupGuidePage} flag and dynamically
     * interacts with different controllers based on the operation context.
     */
    public void proceedToPayment() {
        messageController.closePopup(true);
        if (!groupGuidePage) {
            Message msg = new Message(OpCodes.OP_MARK_ORDER_AS_PAID, applicationWindowController.getUser().getUsername(), mostRecentOrder);
            ClientUI.client.accept(msg);

            Message response = ClientCommunicator.msg;
            OpCodes returnOpCode = response.getMsgOpcode();
            if (returnOpCode == OpCodes.OP_DB_ERR) {
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
            if (!(response.getMsgData() instanceof Boolean)) {
                ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
                confirmationPopup.show(applicationWindowController.getRoot());
                return;
            }

            applicationWindowController.loadEmployeesPage("GenerateBillPage");
            Object controller = applicationWindowController.getCurrentActiveController();
            if (controller instanceof GenerateBillController) {
                ((GenerateBillController) controller).showPopUpAfterBillGeneration((Boolean) response.getMsgData(), orderID);
            }
        } else {
            applicationWindowController.loadVisitorsPage("ActiveOrdersPage");
            Object controller = applicationWindowController.getCurrentActiveController();
            if (controller instanceof ActiveOrdersPageController) {
                ((ActiveOrdersPageController) controller).start();
                ((ActiveOrdersPageController) controller).populateTable((ArrayList) (ClientCommunicator.msg.getMsgData()));
            }
        }
    }

    /**
     * Initializes the view with details of a specific order and sets up the page according to whether
     * it's referred post-order or for group guide management. This method populates the UI elements
     * with the order's information, including pricing details before and after discounts, the order date,
     * the number of visitors, and descriptions of the order type and status.
     *
     * @param order             The {@link Order} object containing all necessary data about the current order to be displayed.
     * @param referredPostOrder A boolean flag indicating whether this page is being referred to after an order
     *                          has been placed (true) or in another context such as group guide management (false).
     *                          <p>
     *                          This method calculates the full price and the discounted price (if any discounts are applicable) based on
     *                          the number of visitors and the specific discount type applicable to the order. It also sets textual descriptions
     *                          for the order type and status, catering to various scenarios such as single, family-sized, or group visitations,
     *                          and whether the order was made spontaneously or pre-ordered. The method dynamically adjusts the displayed
     *                          information based on the order's characteristics and the context in which the page is used.
     */
    public void start(Order order, boolean referredPostOrder) {
        mostRecentOrder = order;
        this.groupGuidePage = referredPostOrder;
        this.orderID = order.getOrderID();

        Discount discountType = Discount.getDiscountType(order.getOrderType(), order.getOrderStatus(), false);
        Double fullPrice = discountType !=
                Discount.PREPAID_PREORDERED_GROUP_DISCOUNT && discountType != Discount.PREORDERED_GROUP_DISCOUNT ?
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
            OrderStatus orderStatus = order.getOrderStatus();
            if (orderStatus.equals(OrderStatus.STATUS_SPONTANEOUS_ORDER) || orderStatus.equals(OrderStatus.STATUS_SPONTANEOUS_ORDER_PENDING_PAYMENT)) {
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
