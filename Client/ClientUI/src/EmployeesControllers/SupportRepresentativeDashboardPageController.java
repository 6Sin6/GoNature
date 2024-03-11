package EmployeesControllers;

import CommonClient.controllers.ApplicationWindowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;

public class SupportRepresentativeDashboardPageController {

    @FXML
    private MFXButton checkParkBtn;

    @FXML
    private MFXButton issueBillBtn;

    @FXML
    private MFXButton registerGuideBtn;

    private ApplicationWindowController applicationWindowController;

    public void setApplicationWindowController(ApplicationWindowController applicationWindowController) {
        this.applicationWindowController = applicationWindowController;
    }
}
