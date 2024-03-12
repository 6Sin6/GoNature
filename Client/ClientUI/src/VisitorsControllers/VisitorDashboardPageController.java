package VisitorsControllers;

import CommonClient.Utils;
import CommonClient.controllers.BaseController;
import CommonUtils.InputTextPopup;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class VisitorDashboardPageController extends BaseController {
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

    private InputTextPopup onAuthPopup;
    private final String orderPage = "/VisitorsUI/VisitorOrderVisitationPage.fxml";
    private final String activeOrdersPage = "/VisitorsUI/ActiveOrdersPage.fxml";

    public void onAuth(String id, String path) {
        String strToPrint = "";
        if (!Utils.isIDValid(id)) {
            strToPrint = "Invalid ID ! Try again";
        }
        if (strToPrint.isEmpty() && !id.equals("316165984")) {
            strToPrint = "Wrong ID ! Try again";
        }
        if (strToPrint.isEmpty()) {
            applicationWindowController.setCenterPage(path);
            applicationWindowController.loadMenu(applicationWindowController.getUser());
        }
        onAuthPopup.setErrorLabel(strToPrint);
    }

    @FXML
    void OnClickOrderVisitButton(ActionEvent event) {
        onAuthPopup = new InputTextPopup("Enter ID to Authenticate ", (inputText) -> this.onAuth(inputText, orderPage), 500, 300, true);
        onAuthPopup.show(applicationWindowController.getRoot());
    }


    @FXML
    void OnClickViewOrdersButton(ActionEvent event) {
        onAuthPopup = new InputTextPopup("Enter ID to Authenticate ", (inputText) -> this.onAuth(inputText, activeOrdersPage), 500, 300, true);
        onAuthPopup.show(applicationWindowController.getRoot());
    }


}
