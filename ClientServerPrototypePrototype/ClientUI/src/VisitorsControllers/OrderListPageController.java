//package VisitorsControllers;
//
//import Entities.Order;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.scene.control.Button;
//import javafx.scene.control.TableView;
//import javafx.scene.control.TableColumn;
//import javafx.scene.control.cell.PropertyValueFactory;
//
//
//public class OrderListPageController {
//
//    @FXML
//    private Button editOrderbtn;
//
//    @FXML
//    private TableView<Order> tableView;
//
//    @FXML
//    private TableColumn<Order, String> orderIDParam;
//
//    @FXML
//    private TableColumn<Order, String> orderNameParam;
//
//    @FXML
//    private TableColumn<Order, String> orderEmailParam;
//
//    @FXML
//    private TableColumn<Order, String> orderTelParam;
//
//    @FXML
//    private TableColumn<Order, String> orderParkParam;
//
//    @FXML
//    private TableColumn<Order, String> orderDateParam;
//
//    @FXML
//    private TableColumn<Order, String> orderTimeParam;
//
//    private boolean editingEnabled = false;
//
//    @FXML
//    public void initialize() {
//        orderIDParam.setCellValueFactory(new PropertyValueFactory<>("orderID"));
//        orderNameParam.setCellValueFactory(new PropertyValueFactory<>("numberOfVisitors"));
//        orderEmailParam.setCellValueFactory(new PropertyValueFactory<>("email"));
//        orderTelParam.setCellValueFactory(new PropertyValueFactory<>("telephone"));
//        orderParkParam.setCellValueFactory(new PropertyValueFactory<>("chosenPark"));
//        orderDateParam.setCellValueFactory(new PropertyValueFactory<>("date"));
//        orderTimeParam.setCellValueFactory(new PropertyValueFactory<>("time"));
//    }
//
//    @FXML
//    void editOrderListAction(ActionEvent event) {
//        if (!editingEnabled) {
//            tableView.setEditable(true);
//            editingEnabled = true;
//            editOrderbtn.setText("Save");
//        } else {
//            tableView.setEditable(false);
//            editingEnabled = false;
//            editOrderbtn.setText("Edit");
//        }
//    }
//
//    @FXML
//    void addRow(ActionEvent event) {
//        tableView.getItems().add(new Order("", "", "", "", "", "", ""));
//    }
//
//
//
//    }
//}