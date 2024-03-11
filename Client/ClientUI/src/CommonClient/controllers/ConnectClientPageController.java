package CommonClient.controllers;

import Entities.Role;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;

public class ConnectClientPageController extends BaseController {
    @FXML
    private MFXButton connectBtn;

    @FXML
    private MFXTextField ipTxt;


    public void onConnect() {
        // Todo: perform connection.
        try {
            applicationWindowController.loadDashboardPage(Role.ROLE_GUEST);
        } catch (Exception e) {
            e.printStackTrace();
            // Todo: Set text label to "Something went wrong..."
        }
    }
}
