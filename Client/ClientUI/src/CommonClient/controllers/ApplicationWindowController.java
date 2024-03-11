package CommonClient.controllers;

import EmployeesControllers.DepartmentManagerDashboardPageController;
import EmployeesControllers.ParkEmployeeDashboardPageController;
import EmployeesControllers.ParkManagerDashboardPageController;
import EmployeesControllers.SupportRepresentativeDashboardPageController;
import VisitorsControllers.LoginPageController;
import VisitorsControllers.VisitorDashboardPageController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ApplicationWindowController implements Initializable
{
    @FXML
    private BorderPane mainPane;

    private Parent connectClientPage;

    private Parent departmentManagerDashboardPage;

    private Parent parkEmployeeDashboardPage;

    private Parent parkManagerDashboardPage;

    private Parent supportRepresentativeDashboardPage;

    private Parent menuSider;

    private Parent loginPage;

    private Parent visitorDashboard;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void loadMenu(String username, String role) {
        try {
            if (menuSider == null) {
                FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("/CommonClient/gui/MenuSider.fxml"));
                menuSider = menuLoader.load();
                MenuSiderController menuController = menuLoader.getController();
                menuController.setRole(role);
                menuController.setUsername(username);
                menuController.buildMenuItems();
                menuController.setApplicationWindowController(this);
            }
            mainPane.setLeft(menuSider);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadConnectionPage() {
        try {
            if (connectClientPage == null) {
                FXMLLoader connectClientLoader = new FXMLLoader(getClass().getResource("/CommonClient/gui/ConnectClientPage.fxml"));
                connectClientPage = connectClientLoader.load();
                ConnectClientPageController connectController = connectClientLoader.getController();
                connectController.setApplicationWindowController(this);
            }
            mainPane.getChildren().removeAll();
            mainPane.setCenter(connectClientPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadDepartmentManagerDashboardPage() {
            try {
                if (departmentManagerDashboardPage == null) {
                    FXMLLoader depMgrLoader = new FXMLLoader(getClass().getResource("/EmployeesUI/DepartmentManagerDashboardPage.fxml"));
                    departmentManagerDashboardPage = depMgrLoader.load();
                    DepartmentManagerDashboardPageController depMgrController = depMgrLoader.getController();
                    depMgrController.setApplicationWindowController(this);
                }
                mainPane.getChildren().removeAll();
                mainPane.setCenter(departmentManagerDashboardPage);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public void loadParkManagerDashboardPage() {
        try {
            if (parkManagerDashboardPage == null) {
                FXMLLoader parkMgrLoader = new FXMLLoader(getClass().getResource("/EmployeesUI/ParkManagerDashboardPage.fxml"));
                parkManagerDashboardPage = parkMgrLoader.load();
                ParkManagerDashboardPageController parkMgrController = parkMgrLoader.getController();
                parkMgrController.setApplicationWindowController(this);
            }
            mainPane.getChildren().removeAll();
            mainPane.setCenter(parkManagerDashboardPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadSupportRepresentativeDashboardPage() {
        try {
            if (supportRepresentativeDashboardPage == null) {
                FXMLLoader suppRepresentativeLoader = new FXMLLoader(getClass().getResource("/EmployeesUI/SupportRepresentativeDashboardPage.fxml"));
                supportRepresentativeDashboardPage = suppRepresentativeLoader.load();
                SupportRepresentativeDashboardPageController supportRepresentativeController = suppRepresentativeLoader.getController();
                supportRepresentativeController.setApplicationWindowController(this);
            }
            mainPane.getChildren().removeAll();
            mainPane.setCenter(supportRepresentativeDashboardPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadParkEmployeeDashboardPage() {
        try {
            if (parkEmployeeDashboardPage == null) {
                FXMLLoader parkEmployeeLoader = new FXMLLoader(getClass().getResource("/EmployeesUI/ParkEmployeeDashboardPage.fxml"));
                parkEmployeeDashboardPage = parkEmployeeLoader.load();
                ParkEmployeeDashboardPageController parkEmployeeController = parkEmployeeLoader.getController();
                parkEmployeeController.setApplicationWindowController(this);
            }
            mainPane.getChildren().removeAll();
            mainPane.setCenter(parkEmployeeDashboardPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadVisitorDashboardPage() {
        try {
            if (visitorDashboard == null) {
                FXMLLoader visitorDashboardLoader = new FXMLLoader(getClass().getResource("/VisitorsUI/VisitorDashboardPage.fxml"));
                visitorDashboard = visitorDashboardLoader.load();
                VisitorDashboardPageController visitorDashboardController = visitorDashboardLoader.getController();
                visitorDashboardController.setApplicationWindowController(this);
            }
            mainPane.getChildren().removeAll();
            mainPane.setCenter(visitorDashboard);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadLoginPage() {
        try {
            if (loginPage == null) {
                FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/VisitorsUI/LoginPage.fxml"));
                loginPage = loginLoader.load();
                LoginPageController loginController = loginLoader.getController();
                loginController.setApplicationWindowController(this);
            }
            mainPane.getChildren().remove(menuSider);
            mainPane.getChildren().removeAll();
            menuSider = null;
            mainPane.setCenter(loginPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logout() {
        try {
            // Todo: logout logic
            loadLoginPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadDashboardFactory(String role) {
        switch (role) {
            case "Department Manager":
                loadDepartmentManagerDashboardPage();
                break;
            case "Park Manager":
                loadParkManagerDashboardPage();
                break;
            case "Park Employee":
                loadParkEmployeeDashboardPage();
                break;
            case "Support Representative":
                loadSupportRepresentativeDashboardPage();
                break;
            case "Visitor":
                loadVisitorDashboardPage();
                break;
            default:
                loadLoginPage();
                break;
        }
    }

    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CommonClient/gui/ApplicationWindow.fxml"));
            Parent root = loader.load();
            ApplicationWindowController controller = loader.getController();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/CommonClient/styles.css").toExternalForm());

            Image logo = new Image("/assets/GoNatureLogo.png");
            primaryStage.getIcons().add(logo);
            primaryStage.setTitle("GoNature Parks and Recreation Inc.");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

            controller.loadConnectionPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
