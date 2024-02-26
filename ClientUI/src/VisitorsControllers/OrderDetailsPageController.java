package VisitorsControllers;

import CommonClientUI.UtilsUI;
import Entities.Message;
import Entities.OpCodes;
import Entities.Order;
import VisitorsUI.ClientUI;
import client.ChatClient;
import client.ClientController;
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
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import CommonClientUI.UtilsUI.*;



public class OrderDetailsPageController implements Initializable {
    private Order order;

    @FXML
    private Label lblOrderNumber;
    @FXML
    private Label lblVisitationDate;

    @FXML
    private TextField txtTelephoneNumber;
    @FXML
    private Label lblVisitationTime;
    @FXML
    private Label lblNumberOfVisitors;
    @FXML
    private Label lblEmail;

    @FXML
    private Button btnSave;
    @FXML
    private Button btnback;

    @FXML
    private ComboBox cmbParkName;

    @FXML
    private Label lblStatusMsg;


    ObservableList<String> list;


    protected void loadOrder(Order o1) {
        this.order = o1;
        this.lblOrderNumber.setText((order.getOrderNo()));
        this.txtTelephoneNumber.setText(order.getTelephoneNumber());
        this.lblVisitationTime.setText(UtilsUI.parseVisitTime(order.getVisitationTime()));
        this.lblVisitationDate.setText(UtilsUI.parseVisitDate(order.getVisitationTime()));
        this.cmbParkName.setValue(order.getParkName());
        this.lblNumberOfVisitors.setText(order.getNumberOfVisitors().toString());
        this.lblEmail.setText(order.getEmailAddress());
    }



    protected void loadOrders(ArrayList<Order> ordersList) {
        for (Order o : ordersList) {
            System.out.println(o);
        }

    }

    // creating list of Faculties
    private void setcmbParkName() {
        ArrayList<String> al = new ArrayList<String>();
        al.add("LonaPark");
        al.add("EnGadiPark");
        al.add("OfiraPark");
        list = FXCollections.observableArrayList(al);
        cmbParkName.setItems(list);
    }

    @FXML
    void SaveChange(ActionEvent event) throws Exception {
        String newTelNo = txtTelephoneNumber.getText();
        String newParkName = (String) cmbParkName.getValue();

        if (!IsValidPhone(newTelNo)) {
            lblStatusMsg.setText("Invalid Phone Number!");
            loadOrder(order);
            return;
        }
        Order newOrder = new Order(newParkName,
                order.getOrderNo(),
                order.getVisitationTime(),
                order.getNumberOfVisitors(),
                newTelNo,
                order.getEmailAddress());
        if (!newOrder.equals(order)) {
            Message msg = new Message(OpCodes.UPDATEORDER, newOrder);
            ClientUI.client.accept(msg);
            Message respondMsg = ChatClient.msg;
            if(respondMsg.GetMsgData() instanceof Boolean)
            {
                boolean success = (boolean) respondMsg.GetMsgData();
                if (success) {
                    order = newOrder;
                }
                lblStatusMsg.setText(success ? "Order Updated Successfully!" : "Failed Updating Order");
                loadOrder(order);
            }
        }
        else
        {
            lblStatusMsg.setText("No changes have occurred");
        }
    }

    public void getUpdateBtn() throws ParseException {
        ClientController clientCon = ClientUI.client;
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = parser.parse(lblVisitationDate.getText() + " " + lblVisitationTime.getText());

        Timestamp timestamp = new Timestamp(date.getTime());
    }

    private boolean IsValidPhone(String phoneNumber) {
        String numericPhoneNumber = phoneNumber.replaceAll("[^0-9]", "");
        if (numericPhoneNumber.length() != 10 || !phoneNumber.startsWith("05"))
            return false;
        return true;
    }

    @FXML
    void returnMain(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        ((Node) event.getSource()).getScene().getWindow().hide(); //hiding primary window
        Stage primaryStage = new Stage();
        try {
            TitledPane root = loader.load(getClass().getResource("/VisitorsControllers/DashboardPage.fxml").openStream());
            DashboardPageContoller dashboardPageContoller = loader.getController();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/VisitorsControllers/DashboardPage.css").toExternalForm());
            primaryStage.setOnCloseRequest(e -> Platform.runLater(() -> {
                ClientUI.client.quit();
            }));
            primaryStage.setTitle("DashboardPage");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (NullPointerException e) {
            System.out.println("Error with opening Files");
        }
    }



    public Integer NumOfVisitor(String numOfVisitor) {
        Integer IntNumOfVisitors = Integer.valueOf(numOfVisitor);
        return IntNumOfVisitors;

    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        setcmbParkName();
    }

}
