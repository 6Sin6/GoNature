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
 * This controller manages the UI for displaying and handling visitor orders awaiting confirmation.
 * It includes a TableView to display order details such as order number, park name, number of visitors,
 * telephone, email, date, and time. The controller provides functionality to handle order confirmation
 * by loading the ConfirmVisitationPage for selected orders. It also filters and populates the TableView
 * with orders in the "Pending Confirmation" status. The UI elements include TableView, TableColumns,
 * Label, and a Button for handling orders. The controller implements the Initializable interface to
 * initialize the controller class after the FXML file has been loaded, setting up data bindings and
 * event handlers. It also utilizes common utility methods for parsing visit dates and times. Additionally,
 * the controller sets up row click events to select orders in the TableView and updates the rowIndex accordingly.
 */

public class OrdersWaitingConfirmationController extends BaseController implements Initializable {

    /**
     * TableView for displaying orders in the UI.
     */
    @FXML
    private TableView<Map<String, String>> tableOrders;

    /**
     * TableColumn for displaying the order number in the TableView.
     */
    @FXML
    private TableColumn<Map, String> colOrderNumber;

    /**
     * TableColumn for displaying the park name in the TableView.
     */
    @FXML
    private TableColumn<Map, String> colParkName;

    /**
     * TableColumn for displaying the number of visitors in the TableView.
     */
    @FXML
    private TableColumn<Map, String> colNumberOfVisitors;

    /**
     * TableColumn for displaying the telephone number in the TableView.
     */
    @FXML
    private TableColumn<Map, String> colTelephone;

    /**
     * TableColumn for displaying the email in the TableView.
     */
    @FXML
    private TableColumn<Map, String> colEmail;

    /**
     * TableColumn for displaying the date in the TableView.
     */
    @FXML
    private TableColumn<Map, String> colDate;

    /**
     * TableColumn for displaying the time in the TableView.
     */
    @FXML
    private TableColumn<Map, String> colTime;

    /**
     * Label for displaying status messages in the UI.
     */
    @FXML
    private Label lblStatusMsg;

    /**
     * Index of the selected row in the TableView.
     */
    private int rowIndex;

    /**
     * Button for handling orders in the UI.
     */
    @FXML
    private MFXButton handleOrderbtn;

    /**
     * ArrayList to hold the orders.
     */
    private ArrayList<Order> list = new ArrayList<>();


    /**
     * Resets the UI elements to their default state for cleanup.
     * This method sets the rowIndex to -1 to deselect any selected row in the TableView,
     * and clears any status message displayed in the lblStatusMsg label.
     */
    public void cleanup() {
        rowIndex = -1;
        lblStatusMsg.setText("");
    }


    /**
     * Handles the action event when the "Handle Order" button is clicked.
     * This method checks if an order is selected in the TableView.
     * If an order is selected, it loads the ConfirmVisitationPage and sets the selected order and orders list in the controller.
     * If no order is selected, it displays an error message in the lblStatusMsg label.
     *
     * @param event The ActionEvent representing the user's click on the "Handle Order" button.
     */
    @FXML
    void OnClickHandleOrderButton(ActionEvent event) {
        if (rowIndex == -1) {
            System.out.println("You must select an order");
            lblStatusMsg.setText("You must select an order");
            return;
        }
        Order o1 = list.get(rowIndex);
        applicationWindowController.loadVisitorsPage("ConfirmVisitationPage");
        Object controller = applicationWindowController.getCurrentActiveController();
        if (controller instanceof ConfirmVisitationPageController) {
            ((ConfirmVisitationPageController) controller).setOrder(o1);
            ((ConfirmVisitationPageController) controller).setOrdersList(list);
        }

    }


    /**
     * Initializes the controller after its root element has been completely processed.
     * This method sets up cell value factories for each TableColumn in the TableView to bind data to the corresponding columns.
     * It also configures the TableView to make rows clickable by calling the makeRowClickable method.
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
    }


    /**
     * Starts the process of retrieving and displaying visitor orders in the TableView.
     * This method sends a message to the server to get visitor orders associated with the current user.
     * It handles the response from the server by checking the opcode and data integrity.
     * If successful, it populates the TableView with the retrieved orders after filtering them,
     * and displays them in the UI.
     * If there's an error during communication with the server or if the response is inappropriate,
     * it displays an error message in a ConfirmationPopup.
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
        if (returnOpCode != OpCodes.OP_GET_VISITOR_ORDERS) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        if (!(response.getMsgData() instanceof ArrayList)) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        populateTable(filterOrders((ArrayList<Order>) (ClientCommunicator.msg.getMsgData())));
    }


    /**
     * Populates the TableView with the provided list of orders.
     * This method clears the existing data in the table and then adds new rows based on the provided dataList.
     * It disables the handleOrderbtn if the dataList is empty and displays a message popup accordingly.
     * For each order in the dataList, it creates a row with order details and adds it to the table.
     * The row data includes the order number, park name, number of visitors, telephone, email, date, and time.
     *
     * @param dataList The list of orders to populate the TableView with.
     */
    @FXML
    public void populateTable(ArrayList<Order> dataList) {
        list.clear();
        if (dataList.isEmpty()) {
            handleOrderbtn.setDisable(true);
            MessagePopup popup = new MessagePopup("There are no orders awaiting confirmation", Duration.seconds(5), 600, 150, false);
            popup.show(applicationWindowController.getRoot());
        } else {
            handleOrderbtn.setDisable(false);
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
    }


    /**
     * Configures the TableView to make its rows clickable.
     * This method sets a row factory for the table to handle mouse clicks on rows.
     * When a row is clicked, it retrieves the data of the clicked row and updates the rowIndex accordingly.
     */
    private void makeRowClickable() {
        // Set row factory to handle clicks on rows
        tableOrders.setRowFactory(tv -> {
            TableRow<Map<String, String>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                    Map<String, String> clickedRowData = row.getItem();
                    rowIndex = row.getIndex();
                    System.out.println("Selected row data: " + clickedRowData);
                }
            });
            return row;
        });
    }


    /**
     * Filters the provided list of orders to include only those with the status "Pending Confirmation".
     * This method iterates through the orders and adds those with the status "Pending Confirmation" to a new list.
     *
     * @param orders The list of orders to be filtered.
     * @return The filtered list of orders with the status "Pending Confirmation".
     */
    private ArrayList<Order> filterOrders(ArrayList<Order> orders) {
        ArrayList<Order> filteredOrders = new ArrayList<>();
        for (Order order : orders) {
            if (order.getOrderStatus() == OrderStatus.STATUS_PENDING_CONFIRMATION) {
                filteredOrders.add(order);
            }
        }
        return filteredOrders;
    }

}
