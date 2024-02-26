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
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;


public class OrderDetailsPageController implements Initializable {
    private Order order;
    ObservableList<String> list;

    @FXML
    private Label lblOrderNumber;

    @FXML
    private Label lblVisitationDate;

    @FXML
    private Label lblVisitationTime;

    @FXML
    private Label lblNumberOfVisitors;

    @FXML
    private Label lblEmail;

    @FXML
    private Label lblStatusMsg;

    @FXML
    private TextField txtTelephoneNumber;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnBack;

    @FXML
    private Label lblTitle;

    @FXML
    private VBox vboxPage;

    @FXML
    private ComboBox cmbParkName;


    protected void loadOrder(Order o1) {
        this.order = o1;
        this.lblOrderNumber.setText((order.getOrderNo()));
        this.txtTelephoneNumber.setText(order.getTelephoneNumber());
        this.lblVisitationTime.setText(UtilsUI.parseVisitTime(order.getVisitationTime()));
        this.lblVisitationDate.setText(UtilsUI.parseVisitDate(order.getVisitationTime()));
        this.cmbParkName.setValue(order.getParkName());
        this.lblNumberOfVisitors.setText(order.getNumberOfVisitors().toString());
        this.lblEmail.setText(order.getEmailAddress());
        this.lblTitle.setText("Order no. " + order.getOrderNo());
    }

    private void setcmbParkName() {
        ArrayList<String> al = new ArrayList<String>();
        al.add("Tel-Aviv - Central Park");
        al.add("Karmiel - High Park");
        al.add("Herzliya - Nature Park");
        list = FXCollections.observableArrayList(al);
        cmbParkName.setItems(list);
    }

    @FXML
    void SaveChange(ActionEvent event) throws Exception {
        String newTelNo = txtTelephoneNumber.getText();
        String newParkName = (String) cmbParkName.getValue();

        if (!IsValidPhone(newTelNo)) {
            lblStatusMsg.setText("Invalid Phone Number !");
            loadOrder(order);
            return;
        }

        Order newOrder = new Order(newParkName,
                order.getOrderNo(),
                order.getVisitationTime(),
                order.getNumberOfVisitors(),
                newTelNo.replaceAll("[^0-9]", ""),
                order.getEmailAddress());

        if (!newOrder.equals(order)) {
            Message msg = new Message(OpCodes.UPDATEORDER, newOrder);
            ClientUI.client.accept(msg);
            Message respondMsg = ChatClient.msg;

            if (respondMsg.GetMsgData() instanceof Boolean) {
                boolean success = (boolean) respondMsg.GetMsgData();
                if (success) {
                    order = newOrder;
                }

                lblStatusMsg.setText(success ? "Updated Order Successfully!" : "Update Failed!");
                loadOrder(order);
            }
            return;
        }
        lblStatusMsg.setText("No changes occurred");
    }

    private boolean IsValidPhone(String phoneNumber) {
        String numericPhoneNumber = phoneNumber.replaceAll("[^0-9]", "");
        return numericPhoneNumber.length() == 10 && phoneNumber.startsWith("05");
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
    public void initialize(URL arg0, ResourceBundle arg1) {
        setcmbParkName();
    }
}
