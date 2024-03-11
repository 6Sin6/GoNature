package VisitorsControllers;

import CommonClient.controllers.ApplicationWindowController;
import CommonClient.controllers.BaseController;
import Entities.Role;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class LoginPageController extends BaseController {
    @FXML
    private Text header;

    @FXML
    private MFXButton loginBtn;

    @FXML
    private StackPane loginPage;

    @FXML
    private Pane pane;

    @FXML
    private Label passwordLbl;

    @FXML
    private Separator sep;

    @FXML
    private Label userNameLbl;

    @FXML
    private MFXTextField userNameText;


    public void onLoginClick() {
        applicationWindowController.loadDashboardPage(Role.ROLE_SINGLE_VISITOR);
        applicationWindowController.loadMenu("Eyal123", "Visitor");
    }
}
