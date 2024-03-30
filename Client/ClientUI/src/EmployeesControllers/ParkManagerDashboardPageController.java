package EmployeesControllers;

import CommonClient.controllers.BaseController;
import Entities.ParkBank;
import Entities.ParkManager;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

/**
 * This class represents the controller for the park manager dashboard page.
 * It extends the {@link BaseController} class and provides specific functionality
 * related to managing the park manager dashboard, including changing park parameters,
 * issuing reports, and viewing reports.
 */
public class ParkManagerDashboardPageController extends BaseController {

    /**
     * Represents a JavaFX button used to change park parameters.
     */
    @FXML
    private MFXButton btnChangeParkParameters;

    /**
     * Represents a JavaFX button used to issue reports.
     */
    @FXML
    private MFXButton btnIssueReports;

    /**
     * Represents a JavaFX button used to view reports.
     */
    @FXML
    private MFXButton btnViewReports;

    /**
     * Represents a JavaFX text element for displaying park manager information.
     */
    @FXML
    private Text parkMgrTxt;


    /**
     * Clears the text displayed in the park manager text element.
     * This method is used to reset the park manager information displayed in the GUI.
     */
    public void cleanup() {
        parkMgrTxt.setText("");
    }


    /**
     * Handles the action event triggered by clicking the "ChangeParkParametersButton".
     * This method loads the employees page with a specific controller ("RequestSettingParkParametersPage").
     * If the current active controller is an instance of RequestSettingParkParametersController,
     * it calls the getParkParameters method to retrieve park parameters.
     *
     * @param event The ActionEvent triggered by clicking the "ChangeParkParametersButton".
     */
    @FXML
    public void OnClickChangeParkParametersButton(ActionEvent event) {
        applicationWindowController.loadEmployeesPage("RequestSettingParkParametersPage");
        if (applicationWindowController.getCurrentActiveController() instanceof RequestSettingParkParametersController) {
            ((RequestSettingParkParametersController) applicationWindowController.getCurrentActiveController()).getParkParameters();
        }
    }

    /**
     * Handles the action event triggered by clicking the "IssueReportsButton".
     * This method loads the employees page with a specific controller ("IssueReportsPage").
     * If the current active controller is an instance of IssueReportsController,
     * it calls the start method to initiate the report issuance process.
     *
     * @param event The ActionEvent triggered by clicking the "IssueReportsButton".
     */
    @FXML
    public void OnClickIssueReportsButton(ActionEvent event) {
        applicationWindowController.loadEmployeesPage("IssueReportsPage");
        if (applicationWindowController.getCurrentActiveController() instanceof IssueReportsController) {
            ((IssueReportsController) applicationWindowController.getCurrentActiveController()).start();
        }
    }

    /**
     * Handles the action event triggered by clicking the "ViewReportsButton".
     * This method loads the employees page with a specific controller ("ViewReportsPage").
     * If the current active controller is an instance of ViewReportsPageController,
     * it calls the start method to initiate the viewing of reports.
     *
     * @param event The ActionEvent triggered by clicking the "ViewReportsButton".
     */
    @FXML
    public void OnClickViewReportsButton(ActionEvent event) {
        applicationWindowController.loadEmployeesPage("ViewReportsPage");
        if (applicationWindowController.getCurrentActiveController() instanceof ViewReportsPageController) {
            ((ViewReportsPageController) applicationWindowController.getCurrentActiveController()).start();
        }
    }
}
