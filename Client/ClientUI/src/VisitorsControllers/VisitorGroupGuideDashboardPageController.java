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
    @FXML
    private StackPane StackPane;

    @FXML
    private Pane Pane;

    @FXML
    private Separator sep;

    @FXML
    private Text header;

    @FXML
    private Text text2;

    @FXML
    private Text text1;

    @FXML
    private Text text11;

    @FXML
    private Text text12;

    @FXML
    private Text text1211;

    @FXML
    private ImageView pngOrderVisit;

    @FXML
    private ImageView pngViewOrders;

    @FXML
    private MFXButton btnOrderVisit;

    @FXML
    private MFXButton bntViewOrders;

    public void cleanup() {
        // Nothing to clean up
    }

    @Override
    public String getUserID() {
        VisitorGroupGuide visitorGroupGuide = (VisitorGroupGuide) applicationWindowController.getUser();
        return visitorGroupGuide.getID();
    }

    @FXML
    public void OnClickOrderVisitButton(ActionEvent event) {
        applicationWindowController.loadVisitorsPage("GroupGuideOrderVisitationPage");
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
