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

public class VisitorDashboardPageController extends GeneralVisitorDashboard {
    public VisitorDashboardPageController() {
        super();
    }

    @FXML
    private StackPane StackPane;

    @FXML
    private Pane Pane;

    @FXML
    private Separator sep;

    @FXML
    private Text header;

    @FXML
    private MFXButton btnOrderVisit;

    @FXML
    private MFXButton btnViewOrders;

    @FXML
    private ImageView pngOrderVisit;

    @FXML
    private ImageView pngViewOrders;

    public void cleanup() {
        // Nothing to clean up
    }

    @Override
    public String getUserID() {
        SingleVisitor visitor = (SingleVisitor) applicationWindowController.getUser();
        return visitor.getID();
    }

    @FXML
    public void OnClickOrderVisitButton(ActionEvent event) {
        applicationWindowController.loadVisitorsPage("VisitorOrderVisitationPage");
    }

    @FXML
    public void OnClickViewOrdersButton(ActionEvent event) {
        applicationWindowController.loadVisitorsPage("ActiveOrdersPage");
        Object controller = applicationWindowController.getCurrentActiveController();
        if (controller instanceof ActiveOrdersPageController) {
            ((ActiveOrdersPageController) controller).start();
        }
    }

    @FXML
    public void OnClickViewConfirmationsOrdersButton(ActionEvent event) {
        applicationWindowController.loadVisitorsPage("OrdersWaitingConfirmation");
        Object controller = applicationWindowController.getCurrentActiveController();
        if (controller instanceof OrdersWaitingConfirmationController) {
            ((OrdersWaitingConfirmationController) controller).start();
        }
    }
}
