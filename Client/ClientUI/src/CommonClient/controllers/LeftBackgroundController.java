package CommonClient.controllers;

import Entities.SingleVisitor;
import VisitorsControllers.VisitorOrderVisitationPageController;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class LeftBackgroundController extends BaseController {

    @FXML
    private ImageView btnBackNoMenu;

    @FXML
    void OnClickBackNoMenuButton(MouseEvent event) {
        if(applicationWindowController.getCurrentActiveController() instanceof VisitorOrderVisitationPageController) {
            applicationWindowController.logout();
            applicationWindowController.setCenterPage("/CommonClient/gui/HomePage.fxml");
            return;
        }
        applicationWindowController.setCenterPageForNewVisitor("/VisitorsUI/VisitorOrderVisitationPage.fxml", applicationWindowController.getUser(), "/CommonClient/gui/LeftBackground.fxml");
    }
}
