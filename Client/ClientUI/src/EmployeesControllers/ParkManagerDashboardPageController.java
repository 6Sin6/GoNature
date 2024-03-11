package EmployeesControllers;

import CommonClient.controllers.ApplicationWindowController;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ParkManagerDashboardPageController {

    @FXML
    private Text txtDescription;

    @FXML
    private Text txtHeader;

    @FXML
    private VBox vboxMenu;

    private ApplicationWindowController applicationWindowController;

    public void setApplicationWindowController(ApplicationWindowController applicationWindowController) {
        this.applicationWindowController = applicationWindowController;
    }

}
