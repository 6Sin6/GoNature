package EmployeesControllers;

import CommonClient.ClientUI;
import CommonClient.controllers.BaseController;
import CommonUtils.CommonUtils;
import CommonUtils.ConfirmationPopup;
import Entities.Message;
import Entities.OpCodes;
import Entities.ParkDepartmentManager;
import client.ClientCommunicator;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

import java.util.ArrayList;

/**
 * Controller class responsible for managing the UI and user interactions on the department manager's dashboard page.
 * This page provides options for issuing reports, viewing existing reports, and managing authorization requests for
 * park parameters.
 */
public class DepartmentManagerDashboardPageController extends BaseController {
    /**
     * Button for issuing new reports. When clicked, it initiates the process for the department manager to issue new reports.
     */
    @FXML
    private MFXButton btnIssueReports;

    /**
     * Button for viewing existing reports. When clicked, it allows the department manager to view reports that have already been issued.
     */
    @FXML
    private MFXButton btnViewReports;

    /**
     * Button for viewing requests. When clicked, it enables the department manager to view and manage requests related to park parameters.
     */
    @FXML
    private MFXButton btnViewRequests;

    /**
     * Text field used to display error messages to the department manager. This field becomes visible when an error occurs.
     */
    @FXML
    private Text errorTxt;

    /**
     * Text field displaying the parks under the jurisdiction of the department manager. This provides a quick reference for the manager.
     */
    @FXML
    private Text depMgrParks;

    /**
     * Text field for welcoming the department manager to the application. It may display the manager's name or a generic welcome message.
     */
    @FXML
    private Text depMgrWelcome;


    /**
     * Resets the textual information on the department manager's dashboard to their default states. This method clears the texts
     * of the {@code depMgrParks}, {@code depMgrWelcome}, and {@code errorTxt} fields. It is typically called when preparing the
     * dashboard for a new session or when clearing any previously displayed messages or data to ensure the UI starts in a clean
     * state for the user.
     * <p>
     * Specifically, this cleanup process involves:
     * - Clearing the list of parks managed by the department manager.
     * - Resetting the welcome message intended for the department manager.
     * - Clearing any error messages that were displayed to the department manager.
     * <p>
     * This method is essential for maintaining a clean and user-friendly interface, particularly in scenarios where the department
     * manager may be switching between different tasks or views within the application, and stale or irrelevant information needs
     * to be cleared from the UI.
     */
    public void cleanup() {
        depMgrParks.setText("");
        depMgrWelcome.setText("");
        errorTxt.setText("");
    }


    /**
     * Handles the action triggered by clicking the "Issue Reports" button. This method navigates the user
     * to the Issue Reports Page, where department managers or authorized employees can issue new reports.
     * <p>
     * Upon navigating to the Issue Reports Page, it checks if the current active controller is an instance
     * of {@link IssueReportsController}. If so, it calls the {@code start()} method on that controller to
     * initialize the page's data and UI components as necessary.
     * <p>
     * This approach ensures that when the Issue Reports Page is displayed, it is ready for user interaction,
     * with any necessary preliminary data loading or UI setup already completed.
     *
     * @param event The {@link ActionEvent} associated with the button click. This parameter is not used directly
     *              in the method but is required for compatibility with FXML's event handling.
     */
    @FXML
    public void OnClickIssueReportsButton(ActionEvent event) {
        applicationWindowController.loadEmployeesPage("IssueReportsPage");
        if (applicationWindowController.getCurrentActiveController() instanceof IssueReportsController) {
            ((IssueReportsController) applicationWindowController.getCurrentActiveController()).start();
        }
    }


    /**
     * Triggers the process of navigating to the "View Reports Page" upon clicking the "View Reports" button.
     * This method changes the current view to allow the user, typically a department manager or an employee,
     * to view existing reports.
     * <p>
     * After navigating to the View Reports Page, it performs a check to ensure that the currently active controller
     * is an instance of {@link ViewReportsPageController}. If this check passes, it calls the {@code start()} method
     * on the controller, which initiates any necessary initialization or data loading processes specific to the
     * reports viewing functionality.
     * <p>
     * The method guarantees that the View Reports Page is fully prepared for user interaction, with all relevant
     * reports data loaded and displayed accordingly.
     *
     * @param event The {@link ActionEvent} generated by the button click. While not used directly in the method,
     *              it is necessary for the method signature to be compatible with FXML's event handling mechanism.
     */
    @FXML
    public void OnClickViewReportsButton(ActionEvent event) {
        applicationWindowController.loadEmployeesPage("ViewReportsPage");
        if (applicationWindowController.getCurrentActiveController() instanceof ViewReportsPageController) {
            ((ViewReportsPageController) applicationWindowController.getCurrentActiveController()).start();
        }
    }

    /**
     * Handles the action triggered by clicking the "View Requests" button, transitioning the user to the
     * Authorize Parks Requests Page. This method is intended for users with the appropriate permissions,
     * such as park department managers, to view and manage authorization requests for park parameters.
     * <p>
     * The method attempts to load the Authorize Parks Requests Page and, if successful, retrieves the current
     * active controller as an instance of {@link AuthorizeParksRequestsController}. It then calls the
     * {@code start()} method on the controller to initialize the page's functionality, ensuring that the
     * page is ready for the user to view and respond to the pending requests.
     * <p>
     * If an exception occurs during the page loading or initialization process, an error message is displayed
     * to the user, indicating that something went wrong and suggesting they try again later. This error handling
     * provides feedback to the user in case of unforeseen issues.
     *
     * @param event The {@link ActionEvent} associated with the button click, used to trigger the navigation and
     *              page loading process. This parameter is necessary for the method signature to match FXML's
     *              event handling requirements but is not used directly in the method.
     */
    @FXML
    public void OnClickViewRequestsButton(ActionEvent event) {
        try {
            applicationWindowController.loadEmployeesPage("AuthorizeParksRequestsPage");
            if (applicationWindowController.getCurrentActiveController() instanceof AuthorizeParksRequestsController) {
                AuthorizeParksRequestsController controller = (AuthorizeParksRequestsController) applicationWindowController.getCurrentActiveController();
                controller.start();
            }
        } catch (Exception e) {
            errorTxt.setText("Something went wrong... Try again later.");
        }
    }
}
