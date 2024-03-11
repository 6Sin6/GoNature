package VisitorsControllers;

import CommonClient.controllers.ApplicationWindowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class LoginPageController {
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

    private ApplicationWindowController applicationWindowController;

    public void setApplicationWindowController(ApplicationWindowController applicationWindowController) {
        this.applicationWindowController = applicationWindowController;
    }

    public void onLoginClick() {
        applicationWindowController.loadDashboardFactory("Support Representative");
        applicationWindowController.loadMenu("Eyal123", "Support Representative");
    }
}
