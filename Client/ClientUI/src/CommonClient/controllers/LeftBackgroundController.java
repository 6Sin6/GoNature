package CommonClient.controllers;

import Entities.SingleVisitor;
import VisitorsControllers.VisitorOrderVisitationPageController;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class LeftBackgroundController extends BaseController {

    /**
     * An {@link ImageView} acting as a back button without an accompanying menu.
     * This UI component is intended for navigating to previous pages or specific
     * sections of the application without displaying a traditional navigation menu.
     */
    @FXML
    private ImageView btnBackNoMenu;

    /**
     * Handles the click event on the {@code btnBackNoMenu} button. This method determines
     * the current active controller within the application and navigates accordingly.
     * If the current active controller is an instance of {@link VisitorOrderVisitationPageController},
     * the method logs out the user and redirects to the application's home page. Otherwise, it
     * sets the view to the {@code VisitorOrderVisitationPage} for a new visitor, using the current
     * user's information and a specific background layout.
     *
     * @param event The {@link MouseEvent} triggered by clicking the button. This parameter
     *              is not used directly in the method but is required for event handling
     *              compatibility.
     */
    @FXML
    void OnClickBackNoMenuButton(MouseEvent event) {
        if (applicationWindowController.getCurrentActiveController() instanceof VisitorOrderVisitationPageController) {
            applicationWindowController.logout();
            applicationWindowController.setCenterPage("/CommonClient/gui/HomePage.fxml");
            return;
        }
        applicationWindowController.setCenterPageForNewVisitor("/VisitorsUI/VisitorOrderVisitationPage.fxml", applicationWindowController.getUser(), "/CommonClient/gui/LeftBackground.fxml");
    }
}
