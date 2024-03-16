package VisitorsControllers;

import CommonClient.controllers.BaseController;
import Entities.Order;
import Entities.ParkBank;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import static CommonClient.Utils.parseVisitDate;
import static CommonClient.Utils.parseVisitTime;

public class HandleOrderDetailsPageController extends BaseController {
    @FXML
    private MFXButton btnCancelOrder;

    @FXML
    private MFXButton btnSubmitChanges;

    @FXML
    private Label lblErrorMsg;

    @FXML
    private Text txtDescription;

    @FXML
    private MFXTextField txtEmail;

    @FXML
    private Text txtHeader;

    private Order order;

    public void cleanup() {
        order = null;
        txtEmail.setText("");
    }

    public void setOrder(Order order) {
        this.order = order;

        txtEmail.setText(order.getClientEmailAddress());
    }

    @FXML
    void OnClickCancelOrderButton(ActionEvent event) {

    }

    @FXML
    void OnClickSubmitChangesButton(ActionEvent event) {

    }

}

