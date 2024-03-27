package CommonClient.controllers;

import EmployeesControllers.DepartmentManagerDashboardPageController;
import EmployeesControllers.ParkEmployeeDashboardPageController;
import EmployeesControllers.ParkManagerDashboardPageController;
import EmployeesControllers.SupportRepresentativeDashboardPageController;
import Entities.Role;
import VisitorsControllers.VisitorDashboardPageController;
import VisitorsControllers.VisitorGroupGuideDashboardPageController;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;

import java.io.IOException;

/**
 * This class is the controller for the side menu of the application.
 * It contains the buttons for navigating to different pages and the user's role and username.
 */
public class MenuSiderController extends BaseController {

    /**
     * The button for navigating to the home page.
     */
    @FXML
    public MFXButton homePageBtn;

    /**
     * The button for navigating to the dashboard page.
     */
    @FXML
    public MFXButton dashboardBtn;

    /**
     * The button for logging out the user.
     */
    @FXML
    public MFXButton logoutBtnMenu;

    /**
     * The first menu button. Depending on the user's role, this button may be used to perform different actions.
     */
    @FXML
    private MFXButton btnAct1;

    /**
     * The second menu button. Depending on the user's role, this button may be used to perform different actions.
     */
    @FXML
    private MFXButton btnAct2;

    /**
     * The third menu button. Depending on the user's role, this button may be used to perform different actions.
     */
    @FXML
    private MFXButton btnAct3;

    /**
     * The label for displaying the user's role.
     */
    @FXML
    private Label userRoleLabel;

    /**
     * The label for displaying the user's username.
     */
    @FXML
    private Label usernameLabel;


    /**
     * Logs out the user and resets the menu.
     */
    public void logout() {
        applicationWindowController.logout();
        userRoleLabel.setText("");
        usernameLabel.setText("");
    }

    public void cleanup() {
        // Nothing to clean up in the menu.
    }


    /**
     * Sets the role of the user.
     *
     * @param role the role of the user
     */
    public void setRole(String role) {
        userRoleLabel.setText(role);
    }


    /**
     * Sets the username displayed in the menu.
     *
     * @param username the username to display
     */
    public void setUsername(String username) {
        usernameLabel.setText(username);
    }


    /**
     * Handles the navigation to the dashboard page based on the user's role.
     * Retrieves the role from the userRoleLabel and loads the corresponding dashboard page.
     */
    public void handleHomePageRoute() {
        applicationWindowController.homepage();
        userRoleLabel.setText("");
        usernameLabel.setText("");
    }


    /**
     * Handles the navigation to the dashboard page based on the user's role.
     * Retrieves the role from the userRoleLabel and loads the corresponding dashboard page.
     */
    public void handleDashboardRoute() {
        applicationWindowController.loadDashboardPage(Role.stringToRole(userRoleLabel.getText()));
    }


    /**
     * Returns an instance of the controller associated with the given FXML file.
     *
     * @param fxmlPath the path to the FXML file
     * @return an instance of the controller associated with the given FXML file
     * @throws IOException if there is an error loading the FXML file
     */
    private Object getController(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.load();
        return loader.getController();
    }


    /**
     * Builds the menu items for the side menu based on the user's role.
     *
     * @param appController the main application controller
     * @throws IOException if there is an error loading the FXML files
     */
    public void buildMenuItems(ApplicationWindowController appController) throws IOException {
        switch (userRoleLabel.getText()) {
            case "Park Manager":
                ParkManagerDashboardPageController parkManagerController = (ParkManagerDashboardPageController) getController("/EmployeesUI/ParkManagerDashboardPage.fxml");
                parkManagerController.setApplicationWindowController(appController);
                btnAct1.setText("Set Park Parameters");
                btnAct1.setOnAction(parkManagerController::OnClickChangeParkParametersButton);
                btnAct2.setText("Issue Reports");
                btnAct2.setOnAction(parkManagerController::OnClickIssueReportsButton);
                btnAct3.setVisible(true);
                btnAct3.setText("View Reports");
                btnAct3.setOnAction(parkManagerController::OnClickViewReportsButton);
                break;

            case "Department Manager":
                DepartmentManagerDashboardPageController departmentManagerController = (DepartmentManagerDashboardPageController) getController("/EmployeesUI/DepartmentManagerDashboardPage.fxml");
                departmentManagerController.setApplicationWindowController(appController);
                btnAct1.setText("Manage Requests");
                btnAct1.setOnAction(departmentManagerController::OnClickViewRequestsButton);
                btnAct2.setText("Issue Reports");
                btnAct2.setOnAction(departmentManagerController::OnClickIssueReportsButton);
                btnAct3.setVisible(true);
                btnAct3.setText("View Reports");
                btnAct3.setOnAction(departmentManagerController::OnClickViewReportsButton);
                break;

            case "Park Employee":
                ParkEmployeeDashboardPageController parkEmployeeController = (ParkEmployeeDashboardPageController) getController("/EmployeesUI/ParkEmployeeDashboardPage.fxml");
                parkEmployeeController.setApplicationWindowController(appController);
                btnAct1.setText("Issue Order Bill");
                btnAct1.setOnAction(parkEmployeeController::OnClickGenerateBillButton);
                btnAct2.setText("Check Park Availability");
                btnAct2.setOnAction(parkEmployeeController::OnClickAvailableSpotButton);
                break;

            case "Support Representative":
                SupportRepresentativeDashboardPageController supportRepresentativeController = (SupportRepresentativeDashboardPageController) getController("/EmployeesUI/SupportRepresentativeDashboardPage.fxml");
                supportRepresentativeController.setApplicationWindowController(appController);
                btnAct1.setText("Issue Order Bill");
                btnAct1.setOnAction(supportRepresentativeController::OnClickGenerateBillButton);
                btnAct2.setText("Check Park Availability");
                btnAct2.setOnAction(supportRepresentativeController::OnClickAvailableSpotButton);
                btnAct3.setVisible(true);
                btnAct3.setText("Register a Group Guide");
                btnAct3.setOnAction(supportRepresentativeController::OnClickRegisterGuideButton);
                break;

            case "Visitor Group Guide":
                VisitorGroupGuideDashboardPageController visitorGroupGuideController = (VisitorGroupGuideDashboardPageController) getController("/VisitorsUI/VisitorGroupGuideDashboardPage.fxml");
                visitorGroupGuideController.setApplicationWindowController(appController);
                btnAct1.setText("View Your Orders");
                btnAct1.setOnAction(visitorGroupGuideController::OnClickViewOrdersButton);
                btnAct2.setText("Make an Order");
                btnAct2.setOnAction(visitorGroupGuideController::OnClickOrderVisitButton);
                btnAct3.setVisible(true);
                btnAct3.setText("Awaiting Confirmation");
                btnAct3.setOnAction(visitorGroupGuideController::OnClickViewConfirmationsOrdersButton);
                break;

            case "Visitor":
                VisitorDashboardPageController visitorController = (VisitorDashboardPageController) getController("/VisitorsUI/VisitorDashboardPage.fxml");
                visitorController.setApplicationWindowController(appController);
                btnAct1.setText("View Your Orders");
                btnAct1.setOnAction(visitorController::OnClickViewOrdersButton);
                btnAct2.setText("Make an Order");
                btnAct2.setOnAction(visitorController::OnClickOrderVisitButton);
                btnAct3.setVisible(true);
                btnAct3.setText("Awaiting Confirmation");
                btnAct3.setOnAction(visitorController::OnClickViewConfirmationsOrdersButton);
                break;
        }
    }


    /**
     * Toggles the visibility of the menu buttons based on the given boolean value.
     *
     * @param isDisabled whether the menu buttons should be disabled
     */
    protected void toggleMenuButtons(boolean isDisabled) {
        btnAct1.setDisable(isDisabled);
        btnAct2.setDisable(isDisabled);
        btnAct3.setDisable(isDisabled);
        dashboardBtn.setDisable(isDisabled);
        homePageBtn.setDisable(isDisabled);
        logoutBtnMenu.setDisable(isDisabled);
    }


    /**
     * Toggles the visibility of the menu buttons based on the given boolean values.
     *
     * @param homePageIsDisabled     whether the home page button should be disabled
     * @param dashboardIsDisabled    whether the dashboard button should be disabled
     * @param thirdButtonIsDisabled  whether the third button should be disabled
     * @param fourthButtonIsDisabled whether the fourth button should be disabled
     * @param fifthButtonIsDisabled  whether the fifth button should be disabled
     * @param logOutButtonIsDisabled whether the log out button should be disabled
     */
    protected void toggleMenuButtons(boolean homePageIsDisabled, boolean dashboardIsDisabled,
                                     boolean thirdButtonIsDisabled, boolean fourthButtonIsDisabled,
                                     boolean fifthButtonIsDisabled, boolean logOutButtonIsDisabled) {
        btnAct1.setDisable(thirdButtonIsDisabled);
        btnAct2.setDisable(fourthButtonIsDisabled);
        btnAct3.setDisable(fifthButtonIsDisabled);
        homePageBtn.setDisable(homePageIsDisabled);
        dashboardBtn.setDisable(dashboardIsDisabled);
        logoutBtnMenu.setDisable(logOutButtonIsDisabled);
    }
}
