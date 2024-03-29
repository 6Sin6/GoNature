package VisitorsControllers;

import Entities.VisitorGroupGuide;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class VisitorGroupGuideDashboardPageController extends GeneralVisitorDashboard {
    /**
     * StackPane element defined in the FXML file.
     */
    @FXML
    private StackPane StackPane;

    /**
     * Pane element defined in the FXML file.
     */
    @FXML
    private Pane Pane;

    /**
     * Separator element defined in the FXML file.
     */
    @FXML
    private Separator sep;

    /**
     * Header Text element defined in the FXML file.
     */
    @FXML
    private Text header;

    /**
     * Text2 element defined in the FXML file.
     */
    @FXML
    private Text text2;

    /**
     * Text1 element defined in the FXML file.
     */
    @FXML
    private Text text1;

    /**
     * Text11 element defined in the FXML file.
     */
    @FXML
    private Text text11;

    /**
     * Text12 element defined in the FXML file.
     */
    @FXML
    private Text text12;

    /**
     * Text1211 element defined in the FXML file.
     */
    @FXML
    private Text text1211;

    /**
     * ImageView element for displaying an image related to ordering visit.
     */
    @FXML
    private ImageView pngOrderVisit;

    /**
     * ImageView element for displaying an image related to viewing orders.
     */
    @FXML
    private ImageView pngViewOrders;

    /**
     * Button for ordering a visit.
     */
    @FXML
    private MFXButton btnOrderVisit;

    /**
     * Button for viewing orders.
     */
    @FXML
    private MFXButton bntViewOrders;

    public void cleanup() {
        // Nothing to clean up
    }


    /**
     * Retrieves the user ID of the current visitor group guide.
     *
     * @return The user ID of the current visitor group guide.
     */
    @Override
    public String getUserID() {
        VisitorGroupGuide visitorGroupGuide = (VisitorGroupGuide) applicationWindowController.getUser();
        return visitorGroupGuide.getID();
    }


    /**
     * Handles the action event when the "Order Visit" button is clicked.
     * Loads the "Group Guide Order Visitation Page" in the application window.
     *
     * @param event The action event triggered by clicking the button.
     */
    @FXML
    public void OnClickOrderVisitButton(ActionEvent event) {
        applicationWindowController.loadVisitorsPage("GroupGuideOrderVisitationPage");
    }

    /**
     * Handles the action event when the "View Orders" button is clicked.
     * Loads the "Active Orders Page" in the application window and starts the page if it's the current active controller.
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
     * Handles the action event when the "View Confirmations Orders" button is clicked.
     * Loads the "Orders Waiting Confirmation" page in the application window and starts the page if it's the current active controller.
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
