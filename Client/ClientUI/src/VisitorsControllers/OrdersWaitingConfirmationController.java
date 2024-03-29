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

public class OrdersWaitingConfirmationController extends BaseController implements Initializable {

    @FXML
    private TableView<Map<String, String>> tableOrders;

    @FXML
    private TableColumn<Map, String> colOrderNumber;

    @FXML
    private TableColumn<Map, String> colParkName;

    @FXML
    private TableColumn<Map, String> colNumberOfVisitors;

    @FXML
    private TableColumn<Map, String> colTelephone;

    @FXML
    private TableColumn<Map, String> colEmail;

    @FXML
    private TableColumn<Map, String> colDate;

    @FXML
    private TableColumn<Map, String> colTime;

    @FXML
    private Label lblStatusMsg;

    private int rowIndex;

    @FXML
    private MFXButton handleOrderbtn;

    private ArrayList<Order> list = new ArrayList<>();

    public void cleanup() {
        rowIndex = -1;
        lblStatusMsg.setText("");
    }

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

    @FXML
    public void populateTable(ArrayList<Order> dataList) {
        list.clear();
        if (dataList.isEmpty()) {
            handleOrderbtn.setDisable(true);
            MessagePopup popup = new MessagePopup("There are no orders awaiting confirmation", Duration.seconds(5), 600, 150, false);
            popup.show(applicationWindowController.getRoot());
        }
        else{
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
