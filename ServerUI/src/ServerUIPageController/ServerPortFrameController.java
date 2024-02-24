package ServerUIPageController;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import ServerUIPage.ServerUI;
import VisitorsControllers.StudentFormController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ServerPortFrameController implements Initializable {
    private StudentFormController sfc;

    String temp = "";

    @FXML
    private Button btnExit = null;
    @FXML
    private Button btnStart;
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
    private Button BtnStop;
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

        } else if (getURLComboBox().isEmpty() || getUserName().isEmpty() || getPassword().isEmpty()) {
            addtolog("You must enter a URL, username and password");
            addtolog("Please try again");
        } else {
            ServerUI.runServer( this);
            toggleControllers(true);
        }
    }

    public void start(Stage primaryStage) throws Exception {
            Parent root = FXMLLoader.load(getClass().getResource("/ServerUIPageController/ServerPort.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/ServerUIPageController/ServerPort.css").toExternalForm());
            primaryStage.setTitle("Server");
            primaryStage.setScene(scene);
            primaryStage.show();
    }

    public void getExitBtn(ActionEvent event) throws Exception {
        addtolog("Exit Server");
        System.exit(0);
    }
    @FXML
    void stopServer(ActionEvent event) throws Exception {
        ServerUI.closeServer();
    }

    public void addtolog(String str) {
        System.out.println(str);
        loggerTextArea.appendText(str+"\n");
    }
    private void setURLComboBox() {
        ArrayList<String> UrlComboList = new ArrayList<>();
        UrlComboList.add("localhost");
        UrlComboList.add("192.168.194.1");
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

    public void toggleControllers(boolean flag) {
        btnStart.setDisable(flag);
        TextfieldUserName.setDisable(flag);
        TextFieldPassword.setDisable(flag);
        URLComboBox.setDisable(flag);
        portxt.setDisable(flag);
        BtnStop.setDisable(!flag);
    }
}