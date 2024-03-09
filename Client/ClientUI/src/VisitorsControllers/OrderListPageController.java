package VisitorsControllers;

import CommonClient.Utils;
import Entities.Order;
import VisitorsUI.ClientUI;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;


public class OrderListPageController implements Initializable {
    @FXML
    private Pane pane;

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
    private Button btnEdit;

    @FXML
    private Button btnBack;

    @FXML
    private Label lblStatusMsg;

    private int rowIndex = -1;
    private ArrayList<Order> list;

    @FXML
    void editOrder(ActionEvent event) {
        if (rowIndex == -1) {
            System.out.println("You must select an order");
            lblStatusMsg.setText("You must select an order");
            return;
        }
        FXMLLoader loader = new FXMLLoader();
        try {
            System.out.println("Order Number has been found");
            Order o1 = list.get(rowIndex);
            ((Node) event.getSource()).getScene().getWindow().hide(); //hiding primary window
            Stage primaryStage = new Stage();
            AnchorPane root = loader.load(getClass().getResource("/VisitorsControllers/OrderDetailsPage.fxml").openStream());

            OrderDetailsPageController orderDetailsPageController = loader.getController();
            orderDetailsPageController.loadOrder(o1);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/VisitorsControllers/OrderDetailsPage.css").toExternalForm());

            primaryStage.setOnCloseRequest(e -> Platform.runLater(() -> {
                ClientUI.client.quit();
            }));

            Image windowImage = new Image("/assets/GoNatureLogo.png");
            primaryStage.getIcons().add(windowImage);

            primaryStage.setTitle("GoNature - Order Details");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            System.out.println("error with open stream");
        }
    }

    @FXML
    void returnMain(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader();

        ((Node) event.getSource()).getScene().getWindow().hide();
        Stage primaryStage = new Stage();

        try {
            AnchorPane root = loader.load(getClass().getResource("/VisitorsControllers/DashboardPage.fxml").openStream());

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/VisitorsControllers/DashboardPage.css").toExternalForm());

            primaryStage.setOnCloseRequest(e -> Platform.runLater(() -> {
                ClientUI.client.quit();
            }));

            Image windowImage = new Image("/assets/GoNatureLogo.png");
            primaryStage.getIcons().add(windowImage);

            primaryStage.setTitle("GoNature - Dashboard");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (NullPointerException e) {
            System.out.println("Error with opening Files");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colOrderNumber.setCellValueFactory(new MapValueFactory<>("Order Number"));
        colParkName.setCellValueFactory(new MapValueFactory<>("Park Name"));
        colNumberOfVisitors.setCellValueFactory(new MapValueFactory<>("Number Of Visitors"));
        colTelephone.setCellValueFactory(new MapValueFactory<>("Telephone"));
        colEmail.setCellValueFactory(new MapValueFactory<>("Email"));
        colDate.setCellValueFactory(new MapValueFactory<>("Date"));
        colTime.setCellValueFactory(new MapValueFactory<>("Time"));

        // Set row factory to handle clicks on rows
        tableOrders.setRowFactory(tv -> {
            TableRow<Map<String, String>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                    Map<String, String> clickedRowData = row.getItem();
                    rowIndex = row.getIndex();
                    // Handle the clicked row data, e.g., display it
                    System.out.println("Selected row data: " + clickedRowData);
                }
            });
            return row;
        });
    }

    @FXML
    public void populateTable(ArrayList<Order> dataList) {
        ObservableList<Map<String, String>> tableData = FXCollections.observableArrayList();
        this.list = dataList;
        for (Order item : dataList) {
            Timestamp orderTimeStamp = item.getVisitationTime();
            String date = Utils.parseVisitDate(orderTimeStamp);
            String time = Utils.parseVisitTime(orderTimeStamp);
            Map<String, String> row = new HashMap<>();
            row.put("Order Number", item.getOrderNo());
            row.put("Park Name", item.getParkName());
            row.put("Number Of Visitors", item.getNumberOfVisitors().toString());
            row.put("Telephone", item.getTelephoneNumber());
            row.put("Email", item.getEmailAddress());
            row.put("Date", date);
            row.put("Time", time.substring(0, time.length() - 3));
            tableData.add(row);
        }
        tableOrders.setItems(tableData);
    }
}
