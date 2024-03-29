package ServerUIPageController;

import ServerUIPage.ServerUI;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * This class is the controller for the server user interface.
 * It implements the Initializable interface, which means it can provide an initialize method
 * that gets called after all @FXML annotated members have been injected.
 */
public class ServerUIFrameController implements Initializable {
    /**
     * ObservableList that holds the list of strings.
     */
    ObservableList<String> list;

    /**
     * Button for exiting the application.
     */
    @FXML
    private Button btnExit = null;

    /**
     * Button for starting the application.
     */
    @FXML
    private Button btnStart;

    /**
     * Label for the list.
     */
    @FXML
    private Label lbllist;

    /**
     * TextArea for logging.
     */
    @FXML
    private TextArea loggerTextArea;

    /**
     * Label for the logger.
     */
    @FXML
    private Label lblLogger;

    /**
     * Label for the SQL user.
     */
    @FXML
    private Label lblSQLUser;

    /**
     * Label for the SQL password.
     */
    @FXML
    private Label lblSQLPassword;

    /**
     * Label for the SQL URL.
     */
    @FXML
    private Label lblSQLURL;

    /**
     * ComboBox for the URL.
     */
    @FXML
    private ComboBox URLComboBox;

    /**
     * TextField for the user name.
     */
    @FXML
    private TextField TextfieldUserName;

    /**
     * PasswordField for the password.
     */
    @FXML
    private PasswordField TextFieldPassword;

    /**
     * Button for stopping the application.
     */
    @FXML
    private Button BtnStop;

    /**
     * Button for importing users.
     */
    @FXML
    private MFXButton importBtn;

    /**
     * TextField for the port.
     */
    @FXML
    private TextField portxt;

    /**
     * TableView for the clients.
     */
    @FXML
    private TableView<Map> tableClients;

    /**
     * TableColumn for the client's name.
     */
    @FXML
    private TableColumn<Map, String> colName;

    /**
     * TableColumn for the client's IP.
     */
    @FXML
    private TableColumn<Map, String> colIP;

    /**
     * TableColumn for the client's status.
     */
    @FXML
    private TableColumn<Map, String> colStatus;

    /**
     * Text for the import users.
     */
    @FXML
    private Text importUsersTxt;

    /**
     * This method returns the port number entered by the user.
     *
     * @return The port number as a string.
     */
    public String getPort() {
        return portxt.getText();
    }


    /**
     * This method is called when the "Done" button is clicked.
     * It validates the input fields and starts the server if the input is valid.
     *
     * @param event The ActionEvent object representing the button click event.
     */
    public void Done(ActionEvent event) {
        String p;
        p = getPort();
        if (p.trim().isEmpty()) {
            addtolog("You must enter a port number");

        } else if (getURLComboBox().isEmpty() || getUserName().isEmpty() || getPassword().isEmpty()) {
            addtolog("You must enter a URL, username and password");
            addtolog("Please try again");
        } else {
            ServerUI.runServer(this);
            boolean userAvailableFlag = ServerUI.checkUsersAvailability(this);
            if (userAvailableFlag) {
                importUsersTxt.setText("");
                importBtn.setDisable(false);
            } else {
                importUsersTxt.setText("Users already exist in the database");
            }
        }
    }

    /**
     * This method is called to start the server user interface.
     *
     * @param primaryStage The primary stage for this application.
     * @throws Exception If an error occurs while starting the server user interface.
     */
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/ServerUIPageController/ServerUIFrame.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/ServerUIPageController/ServerUIFrame.css").toExternalForm());
        Image windowImage = new Image("/assets/GoNatureServerLogo.png");

        primaryStage.getIcons().add(windowImage);

        primaryStage.setTitle("GoNature - Server");

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * This method sets the default values for the input fields.
     */
    public void setDefaultValues() {
        this.TextFieldPassword.setText("Aa123456");
        this.TextfieldUserName.setText("root");
        this.portxt.setText("5555");
        // Set default value for the ComboBox to "localhost" if it exists in the items list
        String defaultURL = "localhost";
        if (list.contains(defaultURL)) {
            this.URLComboBox.setValue(defaultURL);
        } else {
            // Handle the case where the default value doesn't exist in the items list
            // You might want to set a different default value or handle this case differently
            System.err.println("Default URL not found in the list: " + defaultURL);
        }
    }

    /**
     * This method is called when the "Exit" button is clicked.
     * It logs the exit event and then exits the server.
     *
     * @param event The ActionEvent object representing the button click event.
     */
    @FXML
    public void getExitBtn(ActionEvent event) {
        addtolog("Exit Server");
        System.exit(0);
    }

    /**
     * This method is called when the "Stop" button is clicked.
     * It stops the server and disables the "Import Users" button.
     *
     * @param event The ActionEvent object representing the button click event.
     */
    @FXML
    void stopServer(ActionEvent event) {
        ServerUI.closeServer();
        importUsersTxt.setText("Start server to import Users");
        importBtn.setDisable(true);
    }

    /**
     * This method adds a string to the log.
     *
     * @param str The string to be added to the log.
     */
    public synchronized void addtolog(String str) {
        System.out.println(str);
        Platform.runLater(() -> loggerTextArea.appendText(str + "\n"));
    }

    /**
     * This method sets the items for the URLComboBox.
     */
    private void setURLComboBox() {
        ArrayList<String> UrlComboList = new ArrayList<>();
        UrlComboList.add("localhost");
        UrlComboList.add("192.168.194.1");
        list = FXCollections.observableArrayList(UrlComboList);
        URLComboBox.setItems(list);
    }

    /**
     * This method returns the selected URL from the URLComboBox.
     *
     * @return The selected URL as a string.
     */
    public String getURLComboBox() {
        return (String) URLComboBox.getValue();
    }

    /**
     * This method returns the entered username.
     *
     * @return The entered username as a string.
     */
    public String getUserName() {
        return TextfieldUserName.getText();
    }

    /**
     * This method returns the entered password.
     *
     * @return The entered password as a string.
     */
    public String getPassword() {
        return TextFieldPassword.getText();
    }

    /**
     * This method is called after all @FXML annotated members have been injected.
     * It sets the items for the URLComboBox and the default values for the input fields.
     *
     * @param arg0 The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param arg1 The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        setURLComboBox();
        setDefaultValues();
        colName.setCellValueFactory(new MapValueFactory<>("name"));
        colIP.setCellValueFactory(new MapValueFactory<>("ip"));
        colStatus.setCellValueFactory(new MapValueFactory<>("status"));
        tableClients.setRowFactory(tv -> {
            TableRow<Map> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                    Map clickedRowData = row.getItem();
                    // Handle the clicked row data, e.g., display it
                    System.out.println("Selected row data: " + clickedRowData);
                }
            });
            return row;
        });
    }

    /**
     * This method resets the tableClients TableView by clearing all its items.
     */
    public void resetTableClients() {
        tableClients.getItems().clear();
    }

    /**
     * This method adds a row to the tableClients TableView.
     *
     * @param name The name to be added to the row.
     * @param ip The IP to be added to the row.
     */
    public void addRow(String name, String ip) {
        Map<String, String> newRow = new HashMap<>();
        newRow.put("name", name);
        newRow.put("ip", ip);
        newRow.put("status", "Connected");

        tableClients.getItems().add(newRow);
    }

    /**
     * This method removes a row from the tableClients TableView by IP.
     *
     * @param ip The IP of the row to be removed.
     */
    public void removeRowByIP(String ip) {
        // Use removeIf with a predicate to remove rows matching the condition
        tableClients.getItems().removeIf(row -> ip.equals(row.get("ip")));
    }

    /**
     * This method toggles the disable property of the controllers.
     *
     * @param flag The flag indicating whether to disable the controllers.
     */
    @FXML
    public void toggleControllers(boolean flag) {
        btnStart.setDisable(flag);
        TextfieldUserName.setDisable(flag);
        TextFieldPassword.setDisable(flag);
        URLComboBox.setDisable(flag);
        portxt.setDisable(flag);
        BtnStop.setDisable(!flag);
    }

    /**
     * This method is called when the "Import Users" button is clicked.
     * It initializes the import simulator and disables the "Import Users" button.
     *
     * @param event The ActionEvent object representing the button click event.
     * @throws Exception If an error occurs while initializing the import simulator.
     */
    @FXML
    void importUsers(ActionEvent event) throws Exception {
        try {
            ServerUI.initializeImportSimulator(this);
            importBtn.setDisable(true);
            importUsersTxt.setText("Users imported successfully!");
        } catch (Exception e) {
            addtolog(e.getMessage());
        }
    }
}