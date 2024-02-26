package VisitorsControllers;

import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

import Entities.Message;
import Entities.OpCodes;
import Entities.Order;
import VisitorsUI.ClientUI;
import client.ClientController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class OrderDetailsPageController implements Initializable {
    private Order order;
    @FXML
    private Label orderNumber ;
    @FXML
    private Button returnBtn;

    @FXML
    private Text orderNumbertxt;
    @FXML
    private TextField parkNametxt;
    @FXML
    private TextField telephoneNumbertxt;
    @FXML
    private TextField visitationTimetxt;
    @FXML
    private TextField numberOfVisitorstxt;
    @FXML
    private TextField Emailtxt;

    @FXML
    private Label lblName;
    @FXML
    private Label lblSurname;
    @FXML
    private Label lblFaculty;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnclose=null;

    @FXML
    private ComboBox cmbParkName;

    ObservableList<String> list;



    protected void loadOrder (Order o1) {
        this.order=o1;
        this.orderNumbertxt.setText((order.getOrderNo()));
        this.telephoneNumbertxt.setText(order.getTelephoneNumber());
        this.visitationTimetxt.setText((order.getVisitationTime()).toString());
        this.cmbParkName.setValue(order.getParkName());
        this.numberOfVisitorstxt.setText(order.getParkName());
        this.Emailtxt.setText(order.getEmailAddress());
    }
    protected void loadOrders(ArrayList<Order> ordersList){
        for (Order o : ordersList){
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
    void SaveChange(ActionEvent event) {
        ArrayList<String> Datasend= new ArrayList<>();
        if(!IsValidPhone(telephoneNumbertxt.getText())) {
            System.out.println("Wrong Phone Number");
            return;
        }
        Datasend.add(telephoneNumbertxt.getText());
        Datasend.add((String)cmbParkName.getValue());
        ClientUI.client.accept(Datasend);
    }
    private boolean IsValidPhone(String phoneNumber){
        String numericPhoneNumber = phoneNumber.replaceAll("[^0-9]", "");
        if( numericPhoneNumber.length()!=10 || phoneNumber.startsWith("05"))
            return false;
        return true;
    }

    @FXML
    void returnMain(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        ((Node)event.getSource()).getScene().getWindow().hide(); //hiding primary window
        Stage primaryStage = new Stage();
        try {
            Pane root = loader.load(getClass().getResource("/VisitorsControllers/DashboardPage.fxml").openStream());
            DashboardPageContoller dashboardPageContoller = loader.getController();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/VisitorsControllers/DashboardPage.css").toExternalForm());
            primaryStage.setTitle("DashboardPage");
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch (NullPointerException e ){
            System.out.println( "Error with opening Files");
        }
    }

    public void getUpdateBtn(ActionEvent event) throws Exception {
        ClientController clientCon = ClientUI.client;
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = parser.parse(visitationTimetxt.getText());
        Timestamp timestamp = new Timestamp(date.getTime());

        Order order = new Order((String) cmbParkName.getValue(), orderNumbertxt.getText(), timestamp, NumOfVisitor(numberOfVisitorstxt.getText()), telephoneNumbertxt.getText(), Emailtxt.getText());
        clientCon.accept(new Message(OpCodes.GETORDERBYID,order));
    }

    public Integer NumOfVisitor(String numOfVisitor){
        Integer IntNumOfVisitors = Integer.valueOf(numOfVisitor);
        return IntNumOfVisitors;

    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        setcmbParkName();
    }

}
