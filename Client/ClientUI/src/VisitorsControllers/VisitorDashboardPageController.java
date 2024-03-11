package VisitorsControllers;

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
    private MFXButton btnOrderVisit;

    @FXML
    private MFXButton btnViewOrders;

    @FXML
    private ImageView pngOrderVisit;

    @FXML
    private ImageView pngViewOrders;

    @FXML
    void OnClickOrderVisitButton(ActionEvent event) {
        InputTextPopup inputtextPopup = new InputTextPopup("Enter ID to Authintcate ",(inputText) -> {System.out.println("Input ID: " + inputText);});
        inputtextPopup.show(StackPane);


    }



    @FXML
    void OnClickViewOrdersButton(ActionEvent event) {

    }


}
