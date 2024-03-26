package CommonClient.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class LeftBackgroundController extends BaseController {

    @FXML
    private ImageView btnBackNoMenu;

    @FXML
    void OnClickBackNoMenuButton(MouseEvent event) {
        applicationWindowController.logout();
        applicationWindowController.setCenterPage("/CommonClient/gui/HomePage.fxml");
    }

}
