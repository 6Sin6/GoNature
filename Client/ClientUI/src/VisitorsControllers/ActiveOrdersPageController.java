package VisitorsControllers;

import CommonClient.controllers.BaseController;
import CommonUtils.MessagePopup;
import Entities.Order;
import Entities.ParkBank;
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

import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;


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

    private int rowIndex;

    private ArrayList<Order> list;

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
        applicationWindowController.loadVistorsPage("HandleOrderDetailsPage");


//        try {
//            System.out.println("Order Number has been found");
//            Order o1 = list.get(rowIndex);
//            ((Node) event.getSource()).getScene().getWindow().hide(); //hiding primary window
//            Stage primaryStage = new Stage();
//            AnchorPane root = loader.load(getClass().getResource("/VisitorsControllers/OrderDetailsPage.fxml").openStream());
//
//            OrderDetailsPageController orderDetailsPageController = loader.getController();
//            orderDetailsPageController.loadOrder(o1);
//
//            Scene scene = new Scene(root);
//            scene.getStylesheets().add(getClass().getResource("/VisitorsControllers/OrderDetailsPage.css").toExternalForm());
//
//            primaryStage.setOnCloseRequest(e -> Platform.runLater(() -> {
//                ClientUI.client.quit();
//            }));
//
//            Image windowImage = new Image("/assets/GoNatureLogo.png");
//            primaryStage.getIcons().add(windowImage);
//
//            primaryStage.setTitle("GoNature - Order Details");
//            primaryStage.setScene(scene);
//            primaryStage.show();
//        } catch (Exception e) {
//            System.out.println("error with open stream");
//        }
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

    @FXML
    public void populateTable(ArrayList<Order> dataList) {
        if (dataList.isEmpty()){
            MessagePopup popup = new MessagePopup("No active orders", Duration.seconds(5), 300, 150, false, false);
            popup.show(applicationWindowController.getRoot());
        }
        rowIndex = -1;
        ObservableList<Map<String, String>> tableData = FXCollections.observableArrayList();
        this.list = dataList;
        for (Order item : dataList) {
            Timestamp orderTimeStamp = item.getVisitationDate();
            String date = CommonClient.Utils.parseVisitDate(orderTimeStamp);
            String time = CommonClient.Utils.parseVisitTime(orderTimeStamp);
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


}


