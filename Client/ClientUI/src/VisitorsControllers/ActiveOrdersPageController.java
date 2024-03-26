package VisitorsControllers;

import CommonClient.ClientUI;
import CommonClient.controllers.BaseController;
import CommonUtils.ConfirmationPopup;
import CommonUtils.MessagePopup;
import CommonUtils.*;
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
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static CommonUtils.CommonUtils.parseVisitDate;
import static CommonUtils.CommonUtils.parseVisitTime;


public class ActiveOrdersPageController extends BaseController implements Initializable {

    @FXML
    private Pane bntHandleOrder;

    @FXML
    private Text txtHeader;

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
    private MFXButton handleOrderbtn;

    @FXML
    private Label lblStatusMsg;
    @FXML
    private Rectangle errorRec;

    private int rowIndex;

    private ArrayList<Order> list = new ArrayList<>();

    public void cleanup() {
        rowIndex = -1;
        lblStatusMsg.setText("");
        errorRec.setVisible(false);

    }

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

    private Order findOrderById(String orderId) {
        for (Order order : list) {
            if (order.getOrderID().equals(orderId)) {
                return order;
            }
        }
        return null;
    }


}


