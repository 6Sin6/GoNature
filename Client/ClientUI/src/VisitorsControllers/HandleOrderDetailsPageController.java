package VisitorsControllers;

import CommonClient.controllers.BaseController;
import Entities.Order;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;



public class HandleOrderDetailsPageController extends BaseController {

    @FXML
    private TextField txtEmail;

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

