package VisitorsControllers;

import Entities.Message;
import Entities.OpCodes;
import Entities.Order;
import VisitorsUI.ClientUI;
import client.ChatClient;
import client.ClientController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Objects;

public class DashboardPageController {
    private OrderDetailsPageController order;

    @FXML
    private AnchorPane GoNatureDashboardPage;

    @FXML
    private TextField orderIDtxt;

    @FXML
    private TextField IPAddresstxt;

    @FXML
    private Label orderByIDLbl;

    @FXML
    private Button orderByIDbtn = null;

    @FXML
    private Button viewAllOrdersbtn = null;

    @FXML
    private Label lblStatusMsg;

    private String getOrderNo(){return orderIDtxt.getText();}

    private void buildConnection() {
        if (ClientUI.client == null) {
            System.out.println("Connecting to " + IPAddresstxt.getText() + " on port 5555...");
            ClientUI.client = new ClientController(IPAddresstxt.getText(), 5555);
//            this.performHandshake();
        }
    }

    private void performHandshake() {
        Message msg = new Message(OpCodes.SYNC_HANDSHAKE);
        ClientUI.client.accept(msg);
        if (ChatClient.msg.GetMsgOpcode() == OpCodes.SYNC_HANDSHAKE) {
            return;
        }
        System.exit(0); // Server is not running
    }

    @FXML
    void getOrderByID(ActionEvent event) throws Exception {
        String id;

        /* Validate the existence of a client connection, and if none exists, establish it...*/
        this.buildConnection();

        FXMLLoader loader = new FXMLLoader();

        id = getOrderNo();
        if (id.trim().isEmpty()) {
            System.out.println("You must enter an order number");
            lblStatusMsg.setText("You must enter an order number");
            return;
        }

        ClientUI.client.accept(new Message(OpCodes.GETORDERBYID, id));
        if (ChatClient.msg.GetMsgOpcode() != OpCodes.GETORDERBYID || ChatClient.msg.GetMsgData() == null) {
            System.out.println("Order was not found");
            lblStatusMsg.setText("Order " + id + " not found");
            return;
        }

        try {
            Order o1 = (Order) ChatClient.msg.GetMsgData();

            if (Objects.equals(o1.getOrderNo(), "")) {
                System.out.println("Order was not found");
                lblStatusMsg.setText("Order " + id + " not found");
                return;
            }

            System.out.println("Order No. " + id + " found");

            ((Node)event.getSource()).getScene().getWindow().hide();

            Stage primaryStage = new Stage();
            AnchorPane root = loader.load(getClass().getResource("/VisitorsControllers/OrderDetailsPage.fxml").openStream());

            OrderDetailsPageController orderDetailsPageController  = loader.getController();
            orderDetailsPageController.loadOrder(o1);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/VisitorsControllers/OrderDetailsPage.css").toExternalForm());

            primaryStage.setOnCloseRequest(e -> Platform.runLater(()-> {
                ClientUI.client.quit();
            }));

            Image windowImage = new Image("/assets/GoNatureLogo.png");
            primaryStage.getIcons().add(windowImage);

            primaryStage.setTitle("GoNature - Order Details");
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch (Exception e) {
            System.out.println("error with open stream");
        }
    }

    @FXML
    void viewALLOrdersAction(ActionEvent event) {
        /* Validate the existence of a client connection, and if none exists, establish it...*/
        this.buildConnection();

        FXMLLoader loader = new FXMLLoader();
        Object message = new Message(OpCodes.GETALLORDERS,null);
        ClientUI.client.accept(message);

        if (ChatClient.msg.GetMsgOpcode() == OpCodes.GETALLORDERS) {
            try {
                displayAllOrders(event,loader);
                return;
            } catch (Exception e) {
                System.out.println("error with open stream");
            }
        }

        System.out.println("Error retrieving all orders...");
    }


    private void displayAllOrders(ActionEvent event, FXMLLoader loader) throws Exception {
        System.out.println("All orders received");
        ArrayList<Order> OrderList = (ArrayList<Order>) ChatClient.msg.GetMsgData();

        ((Node)event.getSource()).getScene().getWindow().hide();
        Stage primaryStage = new Stage();

        Pane root = loader.load(getClass().getResource("/VisitorsControllers/OrderListPage.fxml").openStream());
        OrderListPageController OrderListPageController  = loader.getController();
        OrderListPageController.populateTable(OrderList);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/VisitorsControllers/OrderListPage.css").toExternalForm());

        primaryStage.setOnCloseRequest(e -> Platform.runLater(()-> {
            ClientUI.client.quit();
        }));

        Image windowImage = new Image("/assets/GoNatureLogo.png");
        primaryStage.getIcons().add(windowImage);

        primaryStage.setTitle("GoNature - Order List");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/VisitorsControllers/DashboardPage.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/VisitorsControllers/DashboardPage.css").toExternalForm());

        Image windowImage = new Image("/assets/GoNatureLogo.png");
        primaryStage.getIcons().add(windowImage);

        primaryStage.setTitle("GoNature - Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}