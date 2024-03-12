package CommonClient.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class HomePageController extends BaseController implements Initializable {

    @FXML
    private MFXButton bookBtn;

    @FXML
    private ImageView centerImg;

    @FXML
    private ImageView img1;

    @FXML
    private ImageView img2;

    @FXML
    private MFXButton signInBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        centerImg.toFront();
        centerImg.setEffect(new DropShadow());
        img1.setEffect(new DropShadow());
        img2.setEffect(new DropShadow());
    }

    public void onButtonClicked() {
        if (applicationWindowController.getUser() != null) {
            applicationWindowController.logout();
            return;
        }
        applicationWindowController.setCenterPage("/CommonClient/gui/LoginPage.fxml");
    }

    public void onBookButtonClicked() {
        if (applicationWindowController.getUser() == null) {
            applicationWindowController.setCenterPage("/CommonClient/gui/LoginPage.fxml");
            return;
        }
        applicationWindowController.loadDashboardPage(applicationWindowController.getUser().getRole());
    }
}
