package CommonClient.controllers;

import Entities.Message;
import Entities.OpCodes;
import Entities.Role;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class MenuSiderController extends BaseController {

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

    public void logout() {
        applicationWindowController.logout();
        userRoleLabel.setText("");
        usernameLabel.setText("");
    }

    public void setRole(String role) {
        userRoleLabel.setText(role);
    }

    public void setUsername(String username) {
        usernameLabel.setText("User: " + username);
    }

    public void handleHomePageRoute() {
        this.logout();
//        applicationWindowController.loadDashboardPage(Role.ROLE_GUEST);
    }

    public void handleDashboardRoute() {
        applicationWindowController.loadDashboardPage(Role.stringToRole(userRoleLabel.getText()));
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
                //btnAct3.setOnAction(event -> applicationWindowController.setCenterPage("/EmployeesUI/RegisterGroupGuidePage.fxml"));
                break;
            case "Visitor":
                btnAct1.setText("View Your Orders");
                btnAct2.setText("Make an Order");
                break;
        }
    }
}
