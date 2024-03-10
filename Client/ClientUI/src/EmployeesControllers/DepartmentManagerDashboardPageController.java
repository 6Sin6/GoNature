package EmployeesControllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class DepartmentManagerDashboardPageController implements Initializable {

    @FXML
    private ImageView appLogo;

    @FXML
    private Label appName;

    @FXML
    private ImageView backButton;

    @FXML
    private MFXButton dashboardBtn;

    @FXML
    private ImageView exitBtn;

    @FXML
    private Text headerTxt;

    @FXML
    private MFXButton homePageBtn;

    @FXML
    private MFXButton issueBtn;

    @FXML
    private Text issueTxt;

    @FXML
    private StackPane logoContainer;

    @FXML
    private MFXButton logoutBtnMenu;

    @FXML
    private VBox menuBox;

    @FXML
    private MFXButton ordersBtn;

    @FXML
    private MFXButton manageBtn;

    @FXML
    private Pane regpane;

    @FXML
    private StackPane stackpane;

    @FXML
    private Label userRoleLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private MFXButton viewRequestsBtn;

    @FXML
    private Text viewRequestsTxt;

    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/EmployeesUI/DepartmentManagerDashboardPage.fxml")));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/CommonClient/styles.css").toExternalForm());

        primaryStage.setTitle("GoNature - Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Set images for back button and app logo
        backButton.setImage(new Image(getClass().getResource("/assets/back_arrow.png").toExternalForm()));
        appLogo.setImage(new Image(getClass().getResource("/assets/GoNatureLogo.png").toExternalForm()));
    }

}
