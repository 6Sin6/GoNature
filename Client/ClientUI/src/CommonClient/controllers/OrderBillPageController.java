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
import EmployeesControllers.GenerateBillController;
import java.util.ArrayList;
import java.util.Date;

public class OrderBillPageController extends BaseController {
    @FXML
    private ImageView backBtn;

    @FXML
    private Pane billPane;

    @FXML
    private Text discountTxt;

    @FXML
    private Text fullPriceTxt;

    @FXML
    private Text dateTxt;

    @FXML
    private Text numVisitorsTxt;

    @FXML
    private Text orderIdTxt;

    @FXML
    private Text orderTypeTxt;

    @FXML
    private Text priceAfterDiscTxt;


    @FXML
    private MFXButton proceedBtn;

    @FXML
    private Text typeDescTxt;

    @FXML
    private Text priceTxt;

    private MessagePopup messageController;
    private boolean groupGuidePage;
    private String orderID;
    private Order mostRecentOrder;

    public void cleanup() {
        discountTxt.setText("");
        fullPriceTxt.setText("");
        numVisitorsTxt.setText("");
        orderIdTxt.setText("");
        orderTypeTxt.setText("");
        priceAfterDiscTxt.setText("");
        typeDescTxt.setText("");
    }


    public void setMessagePopup(MessagePopup messageController) {
        this.messageController = messageController;
    }

    public void closePopup() {
        messageController.closePopup(false);
    }

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

    public void start(Order order, boolean referredPostOrder, boolean prepaid) {
        mostRecentOrder = order;
        this.groupGuidePage = referredPostOrder;
        this.orderID = order.getOrderID();

        Discount discountType = Discount.getDiscountType(order.getOrderType(), order.getOrderStatus(), prepaid);
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
