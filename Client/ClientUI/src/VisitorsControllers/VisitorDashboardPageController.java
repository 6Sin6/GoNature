package VisitorsControllers;

import Entities.SingleVisitor;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

/**
 * This controller manages the dashboard page for single visitors.
 * It handles UI elements such as buttons, text, and images for ordering visits and viewing orders.
 * The controller provides methods to initialize the dashboard, handle button clicks for ordering visits and viewing orders,
 * and retrieve the unique ID of the current user, assumed to be a SingleVisitor.
 * Additionally, it extends the GeneralVisitorDashboard class to inherit common functionality and UI elements.
 */

public class VisitorDashboardPageController extends GeneralVisitorDashboard {
    /**
     * Default constructor for the VisitorDashboardPageController.
     * Initializes a new instance of the controller class.
     */
    public VisitorDashboardPageController() {
        super();
        // Initialization code can be added here if necessary.
    }

    /**
     * The primary container for the dashboard's UI elements, allowing for layered placement.
     */
    @FXML
    private StackPane StackPane;

    /**
     * A pane used within the dashboard, potentially for organizing layout or grouping UI components.
     */
    @FXML
    private Pane Pane;

    /**
     * A UI component used to visually separate sections or elements within the dashboard.
     */
    @FXML
    private Separator sep;

    /**
     * Text element acting as a header or title for the dashboard or a section of it.
     */
    @FXML
    private Text header;

    /**
     * A button that, when clicked, initiates the process for a visitor to order a visit.
     */
    @FXML
    private MFXButton btnOrderVisit;

    /**
     * A button that allows visitors to view their past or current orders.
     */
    @FXML
    private MFXButton btnViewOrders;

    /**
     * An ImageView for displaying an image related to ordering visits.
     * This could be an icon or a small graphic element enhancing the UI's visual appeal.
     */
    @FXML
    private ImageView pngOrderVisit;

    /**
     * An ImageView for displaying an image related to viewing orders.
     * Similar to pngOrderVisit, it enhances the visual appeal and user experience of the dashboard.
     */
    @FXML
    private ImageView pngViewOrders;


    public void cleanup() {
        // Nothing to clean up
    }


    /**
     * Retrieves the unique ID of the current user, assumed to be a SingleVisitor, from the application.
     * This method overrides a superclass or interface method to provide a specific implementation
     * for fetching a visitor's ID.
     *
     * @return A String representing the unique ID of the visitor.
     */
    @Override
    public String getUserID() {
        SingleVisitor visitor = (SingleVisitor) applicationWindowController.getUser();
        return visitor.getID();
    }


    /**
     * Handles the action event when the Order Visit button is clicked.
     * Loads the Visitor Order Visitation Page in the application window.
     *
     * @param event The action event triggered by clicking the button.
     */
    @FXML
    public void OnClickOrderVisitButton(ActionEvent event) {
        applicationWindowController.loadVisitorsPage("VisitorOrderVisitationPage");
    }


    /**
     * Handles the action event when the View Orders button is clicked.
     * Loads the Active Orders Page in the application window and starts the page if it's the current active controller.
     *
     * @param event The action event triggered by clicking the button.
     */
    @FXML
    public void OnClickViewOrdersButton(ActionEvent event) {
        applicationWindowController.loadVisitorsPage("ActiveOrdersPage");
        Object controller = applicationWindowController.getCurrentActiveController();
        if (controller instanceof ActiveOrdersPageController) {
            ((ActiveOrdersPageController) controller).start();
        }
    }


    /**
     * Handles the action event when the View Confirmations Orders button is clicked.
     * Loads the Orders Waiting Confirmation Page in the application window and starts the page if it's the current active controller.
     *
     * @param event The action event triggered by clicking the button.
     */
    @FXML
    public void OnClickViewConfirmationsOrdersButton(ActionEvent event) {
        applicationWindowController.loadVisitorsPage("OrdersWaitingConfirmation");
        Object controller = applicationWindowController.getCurrentActiveController();
        if (controller instanceof OrdersWaitingConfirmationController) {
            ((OrdersWaitingConfirmationController) controller).start();
        }
    }
}
