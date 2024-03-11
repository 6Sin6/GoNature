package VisitorsControllers;

import CommonClient.controllers.BaseController;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class ConfirmVisitationPageController extends BaseController {

    @FXML
    private Text txtHeader;

    @FXML
    private Text txtDescription;

    @FXML
    private Label lblParkName;

    @FXML
    private Label lblNumOfVisitors;

    @FXML
    private Label lblTelephone;

    @FXML
    private Label lblEmail;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblTime;

    @FXML
    private Label lblOrderNumber;

    @FXML
    private MFXButton btnDeclineVisitation;

    @FXML
    private MFXButton btnConfirmVisitation;

    @FXML
    void OnClickConfirmVisitationButton(ActionEvent event) {

    }

    @FXML
    void OnClickDeclineVisitationButton(ActionEvent event) {

    }

}
