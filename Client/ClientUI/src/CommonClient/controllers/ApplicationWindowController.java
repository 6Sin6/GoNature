package CommonClient.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import Entities.Role;



public class ApplicationWindowController implements Initializable {

    @FXML
    private BorderPane mainPane;
    private Map<String, Parent> pagesCache = new HashMap<>();
    private Parent menuSider;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    private Parent loadPage(String fxmlPath) {
        try {
            if (!pagesCache.containsKey(fxmlPath)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent page = loader.load();
                Object controller = loader.getController();
                if (controller instanceof BaseController) { // Assuming all your controllers extend a common BaseController that has setApplicationWindowController method.
                    ((BaseController) controller).setApplicationWindowController(this);
                }
                pagesCache.put(fxmlPath, page);
            }
            return pagesCache.get(fxmlPath);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // or load an error page
        }
    }

    public void loadMenu(String username, String role) {
        try {
            if (menuSider == null) {
                // Ensure FXMLLoader is used to load the FXML and retrieve the controller from it
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/CommonClient/gui/MenuSider.fxml"));
                menuSider = loader.load(); // This loads the FXML and initializes the controller

                // Now retrieve the controller
                MenuSiderController menuController = loader.getController();
                if (menuController instanceof BaseController) {
                    ((BaseController) menuController).setApplicationWindowController(this);
                }
                if (menuController != null) { // This check is technically redundant if load() succeeded without exception
                    menuController.setRole(role);
                    menuController.setUsername(username);
                    menuController.buildMenuItems();
                } else {
                    // Handle the case where the controller wasn't retrieved successfully
                    System.out.println("Failed to retrieve MenuSiderController.");
                }
            }
            mainPane.setLeft(menuSider);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCenterPage(String fxmlPath) {
        mainPane.getChildren().clear(); // Consider if you need to remove all children or just the center.
        Parent page = loadPage(fxmlPath);
        if (page != null) {
            mainPane.setCenter(page);
            mainPane.setLeft(menuSider);
        }
    }

    public void loadDashboardPage(Role role) {
        Map<Role, String> roleToFxmlPath = new HashMap<>();
        roleToFxmlPath.put(Role.ROLE_PARK_DEPARTMENT_MGR, "/EmployeesUI/DepartmentManagerDashboardPage.fxml");
        roleToFxmlPath.put(Role.ROLE_PARK_MGR, "/EmployeesUI/ParkManagerDashboardPage.fxml");
        roleToFxmlPath.put(Role.ROLE_PARK_EMPLOYEE, "/EmployeesUI/ParkEmployeeDashboardPage.fxml");
        roleToFxmlPath.put(Role.ROLE_PARK_SUPPORT_REPRESENTATIVE, "/EmployeesUI/SupportRepresentativeDashboardPage.fxml");
        roleToFxmlPath.put(Role.ROLE_SINGLE_VISITOR, "/VisitorsUI/VisitorDashboardPage.fxml");

        String fxmlPath = roleToFxmlPath.getOrDefault(role, "/VisitorsUI/LoginPage.fxml");
        setCenterPage(fxmlPath);
    }

    public void start(Stage primaryStage) {
        try {
            // Load the main application window directly here
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CommonClient/gui/ApplicationWindow.fxml"));
            Parent root = loader.load(); // This should initialize mainPane as part of the loading process

            // Now that root is loaded, mainPane should be initialized
            ApplicationWindowController controller = loader.getController();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/CommonClient/styles.css").toExternalForm());

            Image logo = new Image("/assets/GoNatureLogo.png");
            primaryStage.getIcons().add(logo);
            primaryStage.setTitle("GoNature Parks and Recreation Inc.");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

            // Now it's safe to manipulate mainPane
            controller.setCenterPage("/CommonClient/gui/ConnectClientPage.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
