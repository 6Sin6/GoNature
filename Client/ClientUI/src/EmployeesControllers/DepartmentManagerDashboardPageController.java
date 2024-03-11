package EmployeesControllers;

import CommonClient.controllers.ApplicationWindowController;
import CommonClient.controllers.BaseController;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class DepartmentManagerDashboardPageController extends BaseController implements Initializable {

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
    private Pane menuBox;

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



    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    }

}
