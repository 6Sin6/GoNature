package CommonClient.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;

public class ConnectClientPageController {
    @FXML
    private MFXButton connectBtn;

    @FXML
    private MFXTextField ipTxt;

    private ApplicationWindowController applicationWindowController;

    public void setApplicationWindowController(ApplicationWindowController applicationWindowController) {
        this.applicationWindowController = applicationWindowController;
    }

    public void onConnect() {
        // Todo: perform connection.
        try {
            applicationWindowController.loadLoginPage();
        } catch (Exception e) {
            e.printStackTrace();
            // Todo: Set text label to "Something went wrong..."
        }
    }
}
