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
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MenuSiderController extends BaseController {

    @FXML
    private MFXButton btnAct1;

    @FXML
    private MFXButton btnAct2;

    @FXML
    private MFXButton btnAct3;

    @FXML
    private Label userRoleLabel;

    @FXML
    private Label usernameLabel;

    public void logout() {
        applicationWindowController.logout();
        userRoleLabel.setText("");
        usernameLabel.setText("");
    }

    public void cleanup() {
        // Nothing to clean up in the menu.
    }

    public void setRole(String role) {
        userRoleLabel.setText(role);
    }

    public void setUsername(String username) {
        usernameLabel.setText(username);
    }

    public void handleHomePageRoute() {
        applicationWindowController.homepage();
        userRoleLabel.setText("");
        usernameLabel.setText("");
    }

    public void handleDashboardRoute() {
        applicationWindowController.loadDashboardPage(Role.stringToRole(userRoleLabel.getText()));
    }

    private Object getController(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        return loader.getController();
    }

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
                btnAct1.setOnAction(supportRepresentativeController::OnClickIssueBillButton);
                btnAct2.setText("Check Park Availability");
                btnAct2.setOnAction(supportRepresentativeController::OnClickCheckParkAvailabilityButton);
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
}
