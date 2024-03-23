package VisitorsControllers;

import CommonClient.ClientUI;
import CommonClient.controllers.BaseController;
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
import javafx.util.Duration;

import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static CommonUtils.CommonUtils.parseVisitDate;
import static CommonUtils.CommonUtils.parseVisitTime;

public class AlternativeTimesTableController extends BaseController implements Initializable {

    @FXML
    private MFXButton LblChooseBtn;
    @FXML
    private Pane bntHandleOrder;

    @FXML
    private TableColumn<Map, String> colDate;

    @FXML
    private TableColumn<Map, String> colTime;

    @FXML
    private Label lblStatusMsg;

    @FXML
    private TableView<Map<String, String>> tableOrders;
    private int rowIndex;
    @FXML
    private Label lblError;
    private ArrayList<Timestamp> list = new ArrayList<>();
    private Order tempOrder;
    private String tempfullName;

    public void cleanup() {
        rowIndex = -1;
        lblError.setText("");
    }

    @FXML
    public void OnClickHandleOrderButton(ActionEvent event) {
        if (rowIndex == -1) {
            System.out.println("You must select an order");
            lblStatusMsg.setText("You must select an order");
            return;
        }
        Timestamp t1 = list.get(rowIndex);
        tempOrder.setVisitationDate(t1);
        tempOrder.setEnteredTime(t1);
        applicationWindowController.loadVisitorsPage("CreateAletrnativeOrder");
        Object controller = applicationWindowController.getCurrentActiveController();
        if (controller instanceof CreateAletrnativeOrderController) {
            ((CreateAletrnativeOrderController) controller).setFields(tempOrder, tempfullName);
        }

    }

    public void start(Order o1, String str) {
        Message send = new Message(OpCodes.OP_GET_AVAILABLE_SPOTS, applicationWindowController.getUser().getUsername(), o1);
        ClientUI.client.accept(send);
        if (ClientCommunicator.msg.getMsgOpcode() == OpCodes.OP_GET_AVAILABLE_SPOTS) {
            populateTable((ArrayList<Timestamp>) (ClientCommunicator.msg.getMsgData()), o1, str);
            lblError.setText("");
        } else if (ClientCommunicator.msg.getMsgOpcode() == OpCodes.OP_DB_ERR) {
            MessagePopup popup = new MessagePopup("ERROR FETCHING DATA", Duration.seconds(5), 300, 150, false);
            popup.show(applicationWindowController.getRoot());
        }
    }

    @FXML
    public void populateTable(ArrayList<Timestamp> dataList, Order o1, String fullName) {
        list.clear();
        if (dataList.isEmpty()) {
            MessagePopup popup = new MessagePopup("No another alternative time", Duration.seconds(5), 300, 150, false);
            popup.show(applicationWindowController.getRoot());
        }
        tempfullName = fullName;
        tempOrder = o1;
        rowIndex = -1;
        ObservableList<Map<String, String>> tableData = FXCollections.observableArrayList();
        for (Timestamp item : dataList) {
            list.add(item);
            String date = parseVisitDate(item);
            String time = parseVisitTime(item);
            Map<String, String> row = new HashMap<>();
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colDate.setCellValueFactory(new MapValueFactory<>("Date"));
        colTime.setCellValueFactory(new MapValueFactory<>("Time"));
        makeRowClickable();
    }
}
