package VisitorsControllers;

import Entities.Message;
import Entities.OpCodes;
import Entities.Order;
import VisitorsUI.ClientUI;
import client.ChatClient;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class DashboardPageContoller {
    private OrderDetailsPageController order ;
    @FXML
    private TitledPane GoNatureOrderPage;

    @FXML
    private TextField orderIDtxt;

    @FXML
    private Label orderByIDLbl;

    @FXML
    private Button orderByIDbtn = null;

    @FXML
    private Button viewAllOrdersbtn=null;

    private String getOrderNo(){return orderIDtxt.getText();}
    @FXML
    void getOrderByID(ActionEvent event) throws Exception {
            String id;
            FXMLLoader loader = new FXMLLoader();

            id=getOrderNo();
            if(id.trim().isEmpty())
            {
                System.out.println("You must enter an order number");
            }
            else
            {
                ClientUI.client.accept(new Message(OpCodes.GETORDERBYID,id));
                if(ChatClient.msg.GetMsgOpcode()!= OpCodes.GETORDERBYID)
                {
                    System.out.println("Order Does Not Found");

                }
                else {
                    try {
                        System.out.println("Order Number has been Found");
                        Order o1 = (Order)ChatClient.msg.GetMsgData();
                        ((Node)event.getSource()).getScene().getWindow().hide(); //hiding primary window
                        Stage primaryStage = new Stage();
                        TitledPane root = loader.load(getClass().getResource("/VisitorsControllers/OrderDetailsPage.fxml").openStream());
                        OrderDetailsPageController orderDetailsPageController  = loader.getController();
                        orderDetailsPageController.loadOrder(o1);
                        Scene scene = new Scene(root);
                        scene.getStylesheets().add(getClass().getResource("/VisitorsControllers/OrderDetailsPage.css").toExternalForm());
                        primaryStage.setOnCloseRequest(e -> Platform.runLater(()-> {
                            ClientUI.client.quit();
                        }));
                        primaryStage.setTitle("OrderDetailsPage");
                        primaryStage.setScene(scene);
                        primaryStage.show();
                    }
                    catch (Exception e){
                        System.out.println("error with open stream");
                    }
                }
            }
        }

    @FXML
    void viewALLOrdersAction(ActionEvent event) {

        FXMLLoader loader = new FXMLLoader();
        Object message = new Message(OpCodes.GETALLORDERS,null);
        ClientUI.client.accept(message);

        if (ChatClient.msg.GetMsgOpcode() == OpCodes.GETALLORDERS) {
            try {
                System.out.println("All orders received");
                ArrayList<Order> OrderList = (ArrayList<Order>) ChatClient.msg.GetMsgData();
                ((Node)event.getSource()).getScene().getWindow().hide(); //hiding primary window
                Stage primaryStage = new Stage();
                Pane root = loader.load(getClass().getResource("/VisitorsControllers/OrderListPage.fxml").openStream());
                OrderListPageController OrderListPageController  = loader.getController();
                OrderListPageController.populateTable(OrderList);
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/VisitorsControllers/OrderListPage.css").toExternalForm());
                primaryStage.setOnCloseRequest(e -> Platform.runLater(()-> {
                    ClientUI.client.quit();
                }));
                primaryStage.setTitle("OrderListPage");
                primaryStage.setScene(scene);
                primaryStage.show();
            }
            catch (Exception e){
                System.out.println("error with open stream");
            }
        }

        System.out.println("Error retrieving all orders...");
    }


    private void displayAllOrders(ActionEvent event,FXMLLoader loader) throws Exception {
        System.out.println("OrdersTable has been Found");
        if(!(ChatClient.msg.GetMsgData() instanceof ArrayList<?>)) {
            System.out.println("Data corruption: OrdersTable not found.");
            return;
        }

        ArrayList<Order> ordersList = (ArrayList<Order>) ChatClient.msg.GetMsgData();
        System.out.println(ordersList);
        ((Node) event.getSource()).getScene().getWindow().hide();
        Stage primaryStage = new Stage();
        Pane root = loader.load(getClass().getResource("/VisitorsControllers/OrderListPageController.fxml").openStream());
        OrderDetailsPageController orderDetailsPageController = loader.getController();
        orderDetailsPageController.loadOrders(ordersList);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/VisitorsControllers/OrderListPageController.css").toExternalForm());
        primaryStage.setTitle("OrderListPage");

        primaryStage.setScene(scene);
        primaryStage.show();

    }
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/VisitorsControllers/DashboardPage.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/VisitorsControllers/DashboardPage.css").toExternalForm());
        primaryStage.setTitle("DashboardPage");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}