package CommonClient.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuSiderController {

    @FXML
    private ImageView appLogo;

    @FXML
    private Label appName;

    @FXML
    private MFXButton dashboardBtn;

    @FXML
    private MFXButton homePageBtn;

    @FXML
    private StackPane logoContainer;

    @FXML
    private MFXButton logoutBtnMenu;

    @FXML
    private MFXButton btnAct1;

    @FXML
    private MFXButton btnAct2;

    @FXML
    private MFXButton btnAct3;

    @FXML
    private Pane menuBox;

    @FXML
    private Label userRoleLabel;

    @FXML
    private Label usernameLabel;

    private ApplicationWindowController applicationWindowController;

    public void setApplicationWindowController(ApplicationWindowController applicationWindowController) {
        this.applicationWindowController = applicationWindowController;
    }

    public void logout() {
        applicationWindowController.logout();
    }

    public void setRole(String role) {
        userRoleLabel.setText(role);
    }

    public void setUsername(String username) {
        usernameLabel.setText("User: " + username);
    }

    public void handleHomePageRoute() {
        applicationWindowController.loadLoginPage();
    }

    public void handleDashboardRoute() {
        applicationWindowController.loadDashboardFactory(userRoleLabel.getText());
    }

    public void buildMenuItems() {
        switch (userRoleLabel.getText()) {
            case "Park Manager":
                btnAct1.setText("Prepare Reports");
//                btnAct1.setOnAction(event -> applicationWindowController.loadPrepareReportsPage());
                btnAct2.setText("Set Park Parameters");
//                btnAct2.setOnAction(event -> applicationWindowController.loadSetParkParametersPage());
                break;
            case "Department Manager":
                btnAct1.setText("Issue Reports");
//                btnAct1.setOnAction(event -> applicationWindowController.loadIssueReportsPage());
                btnAct2.setText("Manage Requests");
//                btnAct2.setOnAction(event -> applicationWindowController.loadManageRequestsPage());
                break;
            case "Park Employee":
                btnAct1.setText("Issue Order Bill");
//                btnAct1.setOnAction(event -> applicationWindowController.loadIssueOrderBillPage());
                btnAct2.setText("Check Park Availability");
//                btnAct2.setOnAction(event -> applicationWindowController.loadParkAvailabilityPage());
                break;
            case "Support Representative":
                btnAct1.setText("Issue Order Bill");
//                btnAct1.setOnAction(event -> applicationWindowController.loadIssueOrderBillPage());
                btnAct2.setText("Check Park Availability");
//                btnAct2.setOnAction(event -> applicationWindowController.loadParkAvailabilityPage());
                btnAct3.setVisible(true);
                btnAct3.setText("Register a Group Guide");
//                btnAct3.setOnAction(event -> applicationWindowController.loadRegisterGuidePage());
                break;
            case "Visitor":
                btnAct1.setText("View Your Orders");
                btnAct2.setText("Make an Order");
                break;
        }
    }
}
