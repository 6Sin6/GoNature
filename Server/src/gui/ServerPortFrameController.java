package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import logic.Faculty;
import logic.Student;
import Server.EchoServer;
import Server.ServerUI;

public class ServerPortFrameController implements Initializable {
    private StudentFormController sfc;

    String temp = "";

    @FXML
    private Button btnExit = null;
    @FXML
    private Button btnDone = null;
    @FXML
    private Label lbllist;
    @FXML
    private TextArea loggerTextArea;

    @FXML
    private Label lblLogger;
    @FXML
    private Label lblSQLUser;

    @FXML
    private Label lblSQLPassword;

    @FXML
    private Label lblSQLURL;

    @FXML
    private ComboBox URLComboBox;

    @FXML
    private TextField TextfieldUserName;

    @FXML
    private TextField TextFieldPassword;


    @FXML
    private TextField portxt;
    ObservableList<String> list;

    public String getPort() {
        return portxt.getText();
    }

    public void Done(ActionEvent event) throws Exception {
        String p;
        p = getPort();
        if (p.trim().isEmpty()) {
            addtolog("You must enter a port number");

        } else {
//			((Node)event.getSource()).getScene().getWindow().hide(); //hiding primary window
//			Stage primaryStage = new Stage();
//			FXMLLoader loader = new FXMLLoader();
            ServerUI.runServer( this);
        }
    }

    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/gui/ServerPort.fxml"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/ServerPort.css").toExternalForm());
        primaryStage.setTitle("Client");
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    public void getExitBtn(ActionEvent event) throws Exception {
        addtolog("exit Academic Tool");
        System.exit(0);
    }

    public void addtolog(String str) {
        String old = loggerTextArea.getText();
        String newstr = old + "\n" + str;
        loggerTextArea.setText(newstr);
    }
    private void setURLComboBox() {
        ArrayList<String> UrlComboList = new ArrayList<>();
        UrlComboList.add("localhost");
        UrlComboList.add("shayddns.ddns.net");
        list = FXCollections.observableArrayList(UrlComboList);
        URLComboBox.setItems(list);
    }
    public String getURLComboBox() {
        return (String) URLComboBox.getValue();
    }
    public String getUserName() {
        return TextfieldUserName.getText();
    }
    public String getPassword() {
        return TextFieldPassword.getText();
    }
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        setURLComboBox();
    }
}