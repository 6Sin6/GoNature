package CommonClient.controllers;

import CommonClient.ClientUI;
import Entities.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;


/**
 * This class is the main controller for the entire application. It is responsible for loading and managing all the other controllers and views.
 * It also acts as the main communication hub between the client and the server.
 */
public class ApplicationWindowController implements Initializable
{
    /**
     * The main pane of the application window. All the other controllers and views are loaded into this pane.
     */
    @FXML
    private BorderPane mainPane;

    /**
     * A cache of all the loaded pages. The key is the FXML path of the page, and the value is a pair of the loaded page and the associated controller.
     */
    private final Map<String, Pair<Parent, Object>> pagesCache = new HashMap<>();

    /**
     * A map of all the dashboard pages for different roles. The key is the role, and the value is the FXML path of the dashboard page.
     */
    private final Map<Role, String> DashBoardMap = new HashMap<>();

    /**
     * A map of all the pages for employees. The key is the name of the FXML file, and the value is the FXML path of the page.
     */
    private final Map<String, String> VisitorsPagesMap = new HashMap<>();

    /**
     * A map of all the pages for visitors. The key is the name of the FXML file, and the value is the FXML path of the page.
     */
    private final Map<String, String> EmployeesPagesMap = new HashMap<>();

    /**
     * The menu on the left side of the application window. It is loaded from the "MenuSider.fxml" file.
     */
    private Parent menuSider;

    /**
     * The currently logged-in user.
     */
    private User user;

    /**
     * Any data that needs to be shared between different controllers can be stored in this field.
     */
    private Object Data;

    /**
     * The controller for the menu on the left side of the application window.
     */
    private MenuSiderController menuController;

    /**
     * The currently active controller. This is the controller that is currently being displayed in the center of the application window.
     */
    private Object currentActiveController;



    /**
     * Initializes the application by setting up the dashBoardMap, employeesPagesMap, and visitorsPagesMap.
     * Additionally, it loads the "ConnectClientPage.fxml" as the initial page.
     *
     * @param location Not used.
     * @param resources Not used.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        setDashBoardMap();
        setVisitorsPagesMap();
        setEmployeesPagesMap();
    }



    /**
     * Retrieves the currently active controller.
     *
     * @return The currently active controller object.
     */
    public Object getCurrentActiveController()
    {
        return currentActiveController;
    }



    /**
     * Sets the currently logged-in user.
     *
     * @param user The user object representing the currently logged-in user.
     */
    private void setUser(User user)
    {
        this.user = user;
    }



    /**
     * Retrieves the currently logged-in user.
     *
     * @return The user object representing the currently logged-in user.
     */
    public User getUser()
    {
        return user;
    }



    /**
     * Retrieves the root of the main pane.
     * This is the main container for all other controllers and views.
     *
     * @return The root BorderPane of the main pane.
     */
    public BorderPane getRoot()
    {
        return this.mainPane;
    }



    /**
     * Loads a page from the FXML file located at the given path.
     * If the page is not already loaded, it is loaded from the FXML file, and its associated controller is instantiated.
     * If the page is already loaded, its associated controller is retrieved from the cache.
     *
     * @param fxmlPath The path to the FXML file representing the page to be loaded.
     * @return The Parent node representing the loaded page.
     */

    private Parent loadPage(String fxmlPath)
    {
        try {
            // If the page we're loading is not cached yet.
            if (!pagesCache.containsKey(fxmlPath)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent page = loader.load();
                Object controller = loader.getController();
                if (controller instanceof BaseController) {
                    ((BaseController) controller).setApplicationWindowController(this);
                }

                // Set new controller as the current active controller.
                currentActiveController = controller;
                pagesCache.put(fxmlPath, new Pair<>(page, controller));
            }

            currentActiveController = pagesCache.get(fxmlPath).getValue();
            return pagesCache.get(fxmlPath).getKey();
        } catch (Exception e)
        {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            return null; // or load an error page
        }
    }



    /**
     * Loads the menu for the given user.
     * If the menu is not already loaded, it is loaded from the "MenuSider.fxml" file.
     * The menu controller is retrieved from the loaded menu, and its properties are set based on the given user.
     * The menu is then set as the left child of the main pane.
     *
     * @param user The user for whom the menu is loaded.
     */
    public void loadMenu(User user)
    {
        setUser(user);
        try {
            if (menuSider == null) {
                // Ensure FXMLLoader is used to load the FXML and retrieve the controller from it
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/CommonClient/gui/MenuSider.fxml"));
                menuSider = loader.load(); // This loads the FXML and initializes the controller

                // Now retrieve the controller
                MenuSiderController menuController = loader.getController();
                this.menuController = menuController;

                if (menuController != null) {
                    (menuController).setApplicationWindowController(this);
                }
                if (menuController != null) { // This check is technically redundant if load() succeeded without exception
                    menuController.setRole(Role.roleToString(user.getRole()));
                    String prefix = user instanceof SingleVisitor ? "Visitor ID: " : "Welcome ";
                    menuController.setUsername(prefix + user.getUsername());
                    menuController.buildMenuItems(this);
                }
            }
            mainPane.setLeft(menuSider);
        } catch (Exception e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }



    /**
     * Toggles the visibility of the menu buttons based on the given boolean value.
     *
     * @param isDisabled A boolean value indicating whether the menu buttons should be disabled (true) or enabled (false).
     */
    public void toggleMenuButtons(boolean isDisabled)
    {
        this.menuController.toggleMenuButtons(isDisabled);
    }




    /**
     * Toggles the visibility of menu buttons based on the specified parameters.
     *
     * @param homePageIsDisabled       True to disable the home page button, false otherwise.
     * @param dashboardIsDisabled      True to disable the dashboard button, false otherwise.
     * @param thirdButtonIsDisabled    True to disable the third button, false otherwise.
     * @param fourthButtonIsDisabled   True to disable the fourth button, false otherwise.
     * @param fifthButtonIsDisabled    True to disable the fifth button, false otherwise.
     * @param logOutButtonIsDisabled   True to disable the logout button, false otherwise.
     */
    public void toggleMenuButtons(boolean homePageIsDisabled, boolean dashboardIsDisabled,
                                  boolean thirdButtonIsDisabled, boolean fourthButtonIsDisabled,
                                  boolean fifthButtonIsDisabled, boolean logOutButtonIsDisabled)
    {
        this.menuController.toggleMenuButtons(homePageIsDisabled, dashboardIsDisabled, thirdButtonIsDisabled,
                fourthButtonIsDisabled, fifthButtonIsDisabled, logOutButtonIsDisabled);
    }



    /**
     * Sets the center page of the application window to the given FXML path.
     * If the page is not already loaded, it is loaded from the FXML file.
     * All other controllers and views are removed from the main pane, and the given page is set as the center child.
     * The menu on the left side is also updated based on the FXML path.
     *
     * @param fxmlPath The path to the FXML file representing the page to be set as the center page.
     */
    public void setCenterPage(String fxmlPath)
    {
        mainPane.getChildren().remove(mainPane.getCenter());
        // Cleanup previous controller.
        if (currentActiveController != null && currentActiveController instanceof BaseController) {
            ((BaseController) currentActiveController).cleanup();
        }
        Parent page = loadPage(fxmlPath);
        if (page != null) {
            mainPane.setCenter(page);
            Parent newMenu = null;
            if (!Objects.equals(fxmlPath, "/CommonClient/gui/LoginPage.fxml") &&
                    !Objects.equals(fxmlPath, "/CommonClient/gui/HomePage.fxml")) {
                newMenu = menuSider;
            }
            mainPane.setLeft(newMenu);
        }
    }



    /**
     * Sets the complete page of the application window, which consists of the center and left pages.
     * The center page is set to the given FXML path, and the left page is set to the given FXML path.
     *
     * @param centerFxmlPath The path to the FXML file representing the center page.
     * @param leftFxmlPath The path to the FXML file representing the left page.
     */
    public void setCompletePage(String centerFxmlPath, String leftFxmlPath)
    {
        // Load center page
        Parent centerPage = loadPage(centerFxmlPath);
        if (centerPage != null) {
            mainPane.setCenter(centerPage);
        }

        // Load left page
        Parent leftPage = loadPage(leftFxmlPath);
        if (leftPage != null) {
            mainPane.setLeft(leftPage);
        }
    }



    /**
     * Sets the center page for a new visitor.
     * This consists of the given FXML path for the center page, the given user, and the FXML path for the left page.
     * The user is set as the currently logged-in user, the center page is loaded, and the left page is loaded.
     *
     * @param fxmlPath The path to the FXML file representing the center page.
     * @param user The user object representing the currently logged-in user.
     * @param leftFxmlPath The path to the FXML file representing the left page.
     */
    public void setCenterPageForNewVisitor(String fxmlPath, User user, String leftFxmlPath)
    {
        setUser(user);
        setCompletePage(fxmlPath,leftFxmlPath);
    }



    /**
     * Logs out the current user.
     * Sends a message to the server to log out the user, and then sets the center page to either the home page for visitors or the login page for employees.
     * Also sets the currently logged-in user to null and removes the menu from the left side of the screen.
     */

    public void logout()
    {
        Object msg = new Message(OpCodes.OP_LOGOUT, user.getUsername(), null);
        ClientUI.client.accept(msg);
        String pathTorRoute;
        if (user instanceof SingleVisitor) {
            pathTorRoute = "/CommonClient/gui/HomePage.fxml";
        } else {
            pathTorRoute = "/CommonClient/gui/LoginPage.fxml";
        }
        setCenterPage(pathTorRoute);
        this.user = null;
        menuSider = null;
    }



    /**
     * Redirects the user to the home page.
     * Sends a message to the server to log out the user and sets the center page to the home page.
     * Also sets the currently logged-in user to null and removes the menu from the left side of the screen.
     */
    public void homepage()
    {
        Object msg = new Message(OpCodes.OP_LOGOUT, user.getUsername(), null);
        ClientUI.client.accept(msg);
        String pathTorRoute = "/CommonClient/gui/HomePage.fxml";
        setCenterPage(pathTorRoute);
        this.user = null;
        menuSider = null;
    }



    /**
     * Initializes the DashBoardMap, which contains the FXML paths of the dashboard pages for different roles.
     * The key is the role, and the value is the FXML path of the dashboard page.
     */
    private void setDashBoardMap()
    {
        DashBoardMap.put(Role.ROLE_PARK_DEPARTMENT_MGR, "/EmployeesUI/DepartmentManagerDashboardPage.fxml");
        DashBoardMap.put(Role.ROLE_PARK_MGR, "/EmployeesUI/ParkManagerDashboardPage.fxml");
        DashBoardMap.put(Role.ROLE_PARK_EMPLOYEE, "/EmployeesUI/ParkEmployeeDashboardPage.fxml");
        DashBoardMap.put(Role.ROLE_PARK_SUPPORT_REPRESENTATIVE, "/EmployeesUI/SupportRepresentativeDashboardPage.fxml");
        DashBoardMap.put(Role.ROLE_SINGLE_VISITOR, "/VisitorsUI/VisitorDashboardPage.fxml");
        DashBoardMap.put(Role.ROLE_VISITOR_GROUP_GUIDE, "/VisitorsUI/VisitorGroupGuideDashboardPage.fxml");
    }



    /**
     * Loads the dashboard page for the given role.
     * If the role is not present in the DashBoardMap, the login page is loaded.
     *
     * @param role The role of the user.
     */
    public void loadDashboardPage(Role role)
    {
        String fxmlPath = DashBoardMap.getOrDefault(role, "/CommonClient/gui/LoginPage.fxml");
        setCenterPage(fxmlPath);
    }



    /**
     * This method initializes the EmployeesPagesMap, which contains the FXML paths of the pages for employees.
     * The key is the name of the FXML file, and the value is the FXML path of the page.
     */
    private void setEmployeesPagesMap()
    {
        EmployeesPagesMap.put("AuthorizeParksRequestsPage", "/EmployeesUI/AuthorizeParksRequestsPage.fxml");
        EmployeesPagesMap.put("CheckAvailableSpotsPage", "/EmployeesUI/CheckAvailableSpotsPage.fxml");
        EmployeesPagesMap.put("GenerateBillPage", "/EmployeesUI/GenerateBillPage.fxml");
        EmployeesPagesMap.put("IssueReportsPage", "/EmployeesUI/IssueReportsPage.fxml");
        EmployeesPagesMap.put("ViewReportsPage", "/EmployeesUI/ViewReportsPage.fxml");
        EmployeesPagesMap.put("PrepareNumberOfVisitorsReportPage", "/EmployeesUI/PrepareNumberOfVisitorsReportPage.fxml");
        EmployeesPagesMap.put("PrepareReportsPage", "/EmployeesUI/PrepareReportsPage.fxml");
        EmployeesPagesMap.put("PrepareUsageReportPage", "/EmployeesUI/PrepareUsageReportPage.fxml");
        EmployeesPagesMap.put("RegisterGroupGuidePage", "/EmployeesUI/RegisterGroupGuidePage.fxml");
        EmployeesPagesMap.put("RequestSettingParkParametersPage", "/EmployeesUI/RequestSettingParkParametersPage.fxml");
        EmployeesPagesMap.put("UnplannedVisitInsertionPage", "/EmployeesUI/UnplannedVisitInsertionPage.fxml");
        EmployeesPagesMap.put("OrderBillPage", "/CommonClient/gui/OrderBillPage.fxml");
    }



    /**
     * Loads an employee page specified by its FXML file name.
     *
     * @param NameOfFxml the name of the FXML file representing the employee page
     */
    public void loadEmployeesPage(String NameOfFxml) {
        String fxmlPath = EmployeesPagesMap.getOrDefault(NameOfFxml, "");
        if (!fxmlPath.isEmpty()) {
            setCenterPage(fxmlPath);
        } else {
            System.out.println("Error");
        }
    }



    /**
     * This method initializes the VisitorsPagesMap, which contains the FXML paths of the pages for visitors.
     * The key is the name of the FXML file, and the value is the FXML path of the page.
     */
    private void setVisitorsPagesMap() {
        VisitorsPagesMap.put("ActiveOrdersPage", "/VisitorsUI/ActiveOrdersPage.fxml");
        VisitorsPagesMap.put("ConfirmVisitationPage", "/VisitorsUI/ConfirmVisitationPage.fxml");
        VisitorsPagesMap.put("GroupGuideOrderVisitationPage", "/VisitorsUI/GroupGuideOrderVisitationPage.fxml");
        VisitorsPagesMap.put("HandleOrderDetailsPage", "/VisitorsUI/HandleOrderDetailsPage.fxml");
        VisitorsPagesMap.put("VisitorOrderVisitationPage", "/VisitorsUI/VisitorOrderVisitationPage.fxml");
        VisitorsPagesMap.put("OrderBillPage", "/CommonClient/gui/OrderBillPage.fxml");
        VisitorsPagesMap.put("CreateAlternativeOrder", "/VisitorsUI/CreateAlternativeOrder.fxml");
        VisitorsPagesMap.put("UpdateOrderDetailsPage", "/VisitorsUI/UpdateOrderDetailsPage.fxml");
        VisitorsPagesMap.put("OrdersWaitingConfirmation", "/VisitorsUI/OrdersWaitingConfirmation.fxml");
        VisitorsPagesMap.put("AlternativeTimesTable", "/VisitorsUI/AlternativeTimesTable.fxml");
        VisitorsPagesMap.put("WaitListPage", "/VisitorsUI/WaitListPage.fxml");
    }



    /**
     * This method loads the visitors page specified by the FXML file name.
     *
     * @param NameOfFxml the name of the FXML file representing the visitors page
     */
    public void loadVisitorsPage(String NameOfFxml) {
        String fxmlPath = VisitorsPagesMap.getOrDefault(NameOfFxml, "");
        if (!fxmlPath.isEmpty()) {
            setCenterPage(fxmlPath);
        } else {
            System.out.println("Error");
        }
    }



    /**
     * Initializes and starts the main application window.
     *
     * @param primaryStage the primary stage representing the main application window
     */
    public void start(Stage primaryStage) {
        try {
            // Load the main application window directly here
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CommonClient/gui/ApplicationWindow.fxml"));
            Parent root = loader.load(); // This should initialize mainPane as part of the loading process


            ApplicationWindowController controller = loader.getController();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/CommonClient/styles.css")).toExternalForm());

            Image logo = new Image("/assets/GoNatureLogo.png");
            primaryStage.getIcons().add(logo);
            primaryStage.setTitle("GoNature Parks and Recreation Inc.");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

            controller.setCenterPage("/CommonClient/gui/ConnectClientPage.fxml");
        } catch (Exception e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }



    /**
     * Returns the data stored in the data field.
     *
     * @return the data stored in the data field
     */
    public Object getData()
    {
        return Data;
    }



    /**
     * Sets the data stored in the data field.
     *
     * @param data the data stored in the data field
     */
    public void setData(Object data)
    {
        Data = data;
    }
}
