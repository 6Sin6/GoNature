package VisitorsControllers;

import CommonClient.ClientUI;
import CommonClient.controllers.BaseController;
import CommonUtils.CommonUtils;
import CommonUtils.ConfirmationPopup;
import CommonUtils.MessagePopup;
import Entities.*;
import client.ClientCommunicator;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static CommonUtils.CommonUtils.parseVisitDate;
import static CommonUtils.CommonUtils.parseVisitTime;

/**
 * This class represents the controller for the active orders page.
 * It allows visitors to view their active orders and handles interactions with order data.
 * The class manages the display of active orders in a table view, allowing visitors to click on orders
 * to view their details and handle them accordingly.
 * The controller initializes the table view with relevant columns and populates it with active orders retrieved from the server.
 * Visitors can click on an order in the table to select it for further actions, such as updating order details.
 * The class also provides methods for handling user actions, such as clicking the "Handle Order" button
 * to update order details or cancelling orders.
 * Additionally, it configures the appearance of table rows based on the status of the orders,
 * highlighting waitlisted and confirmed orders with different background colors.
 * The controller communicates with the server to fetch visitor orders and update order statuses as necessary.
 */

public class ActiveOrdersPageController extends BaseController implements Initializable {

    /**
     * The pane containing the handle order button.
     */
    @FXML
    private Pane bntHandleOrder;

    /**
     * The header text.
     */
    @FXML
    private Text txtHeader;

    /**
     * The table view displaying orders.
     */
    @FXML
    private TableView<Map<String, String>> tableOrders;

    /**
     * The column for order number in the table.
     */
    @FXML
    private TableColumn<Map, String> colOrderNumber;

    /**
     * The column for park name in the table.
     */
    @FXML
    private TableColumn<Map, String> colParkName;

    /**
     * The column for number of visitors in the table.
     */
    @FXML
    private TableColumn<Map, String> colNumberOfVisitors;

    /**
     * The column for telephone number in the table.
     */
    @FXML
    private TableColumn<Map, String> colTelephone;

    /**
     * The column for email in the table.
     */
    @FXML
    private TableColumn<Map, String> colEmail;

    /**
     * The column for date in the table.
     */
    @FXML
    private TableColumn<Map, String> colDate;

    /**
     * The column for time in the table.
     */
    @FXML
    private TableColumn<Map, String> colTime;

    /**
     * The handle order button.
     */
    @FXML
    private MFXButton handleOrderbtn;

    /**
     * The status message label.
     */
    @FXML
    private Label lblStatusMsg;

    /**
     * The rectangle used to display errors.
     */
    @FXML
    private Rectangle errorRec;

    /**
     * The index of the selected row in the table.
     */
    private int rowIndex;

    private ArrayList<Order> list = new ArrayList<>();

    /**
     * Resets the controller's state by clearing the selected row index, status message label, and hiding the error rectangle.
     */
    public void cleanup() {
        rowIndex = -1;
        lblStatusMsg.setText("");
        errorRec.setVisible(false);

    }


    /**
     * Handles the action event when the "Handle Order" button is clicked.
     * If no order is selected, it displays an error message.
     * Otherwise, it loads the "UpdateOrderDetailsPage" and sets the fields with the selected order's details.
     *
     * @param event The action event triggered by clicking the button.
     */
    @FXML
    void OnClickHandleOrderButton(ActionEvent event) {
        if (rowIndex == -1) {
            System.out.println("You must select an order");
            lblStatusMsg.setText("You must select an order");
            errorRec.setVisible(true);
            return;
        }

        Order o1 = list.get(rowIndex);
        applicationWindowController.loadVisitorsPage("UpdateOrderDetailsPage");
        Object controller = applicationWindowController.getCurrentActiveController();
        if (controller instanceof UpdateOrderDetailsPageController) {
            ((UpdateOrderDetailsPageController) controller).setFields(o1);
            ((UpdateOrderDetailsPageController) controller).setOrdersList(list);
        }

    }


    /**
     * Initializes the controller after its root element has been completely processed.
     * Sets up cell value factories for the table columns and makes the table rows clickable.
     *
     * @param location  The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resources The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colOrderNumber.setCellValueFactory(new MapValueFactory<>("Order Number"));
        colParkName.setCellValueFactory(new MapValueFactory<>("Park Name"));
        colNumberOfVisitors.setCellValueFactory(new MapValueFactory<>("Number Of Visitors"));
        colTelephone.setCellValueFactory(new MapValueFactory<>("Telephone"));
        colEmail.setCellValueFactory(new MapValueFactory<>("Email"));
        colDate.setCellValueFactory(new MapValueFactory<>("Date"));
        colTime.setCellValueFactory(new MapValueFactory<>("Time"));
        makeRowClickable();
        errorRec.setVisible(false);
    }


    /**
     * Starts the controller by fetching visitor orders from the server and populating the table with the retrieved data.
     * If there's an error during the process, appropriate error messages are displayed.
     */
    public void start() {
        Message send = new Message(OpCodes.OP_GET_VISITOR_ORDERS, applicationWindowController.getUser().getUsername(), applicationWindowController.getUser());
        ClientUI.client.accept(send);
        Message response = ClientCommunicator.msg;
        OpCodes returnOpCode = response.getMsgOpcode();
        if (returnOpCode == OpCodes.OP_DB_ERR) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        // Checking if the response from the server is inappropriate.
        if (returnOpCode != OpCodes.OP_GET_VISITOR_ORDERS && returnOpCode != OpCodes.OP_GET_VISITOR_GROUP_GROUP_GUIDE_ORDERS) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        if (!(response.getMsgData() instanceof ArrayList)) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        populateTable((ArrayList) (ClientCommunicator.msg.getMsgData()));
        makeRowClickable();
    }


    /**
     * Populates the table with the provided list of orders.
     * Filters out orders with specific statuses and adds valid orders to the table data.
     * If the provided list is empty, it displays a message indicating no active orders.
     *
     * @param dataList The list of orders to populate the table with.
     */
    @FXML
    public void populateTable(ArrayList<Order> dataList) {
        list.clear();
        if (dataList.isEmpty()) {
            MessagePopup popup = new MessagePopup("No active orders", Duration.seconds(5), 600, 150, false);
            popup.show(applicationWindowController.getRoot());
        }
        rowIndex = -1;
        ObservableList<Map<String, String>> tableData = FXCollections.observableArrayList();
        for (Order item : dataList) {
            if (item.getOrderStatus() == OrderStatus.STATUS_CANCELLED ||
                    item.getOrderStatus() == OrderStatus.STATUS_SPONTANEOUS_ORDER ||
                    item.getOrderStatus() == OrderStatus.STATUS_CONFIRMED_AND_ABSENT ||
                    item.getOrderStatus() == OrderStatus.STATUS_FULFILLED) {
                continue;
            }
            list.add(item);
            Timestamp orderTimeStamp = item.getVisitationDate();
            String date = parseVisitDate(orderTimeStamp);
            String time = parseVisitTime(orderTimeStamp);
            Map<String, String> row = new HashMap<>();
            row.put("Order Number", item.getOrderID());
            row.put("Park Name", ParkBank.getParkNameByID(item.getParkID()));
            row.put("Number Of Visitors", item.getNumOfVisitors().toString());
            row.put("Telephone", item.getPhoneNumber());
            row.put("Email", item.getClientEmailAddress());
            row.put("Date", date);
            row.put("Time", time.substring(0, time.length() - 3));
            tableData.add(row);
        }
        tableOrders.setItems(tableData);
        Timestamp orderTimeStamp = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Configures the table rows to be clickable and updates their style based on the order status.
     */
    private void makeRowClickable() {
        tableOrders.setRowFactory(tv -> {
            TableRow<Map<String, String>> row = new TableRow<Map<String, String>>() {
                @Override
                protected void updateItem(Map<String, String> item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setStyle("");
                    } else {
                        String orderId = item.get("Order Number");
                        Order order = findOrderById(orderId);
                        if (order != null && order.getOrderStatus() == OrderStatus.STATUS_WAITLIST) {
                            setStyle("-fx-background-color: #F8D761;");
                        } else if (order != null && order.getOrderStatus() == OrderStatus.STATUS_CONFIRMED_PAID) {
                            setStyle("-fx-background-color:  #9BD38E;");
                        } else {
                            setStyle("");
                        }
                    }
                }
            };
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                    Map<String, String> clickedRowData = row.getItem();
                    rowIndex = row.getIndex();
                    errorRec.setVisible(false);
                    lblStatusMsg.setText("");
                    System.out.println("Selected row data: " + clickedRowData);
                }
            });
            return row;
        });
    }


    /**
     * Finds an order by its unique ID.
     *
     * @param orderId the ID of the order to find
     * @return the order with the specified ID, or {@code null} if no such order exists
     */

    private Order findOrderById(String orderId) {
        for (Order order : list) {
            if (order.getOrderID().equals(orderId)) {
                return order;
            }
        }
        return null;
    }


}


