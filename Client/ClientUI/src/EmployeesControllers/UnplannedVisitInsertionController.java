package EmployeesControllers;

import CommonClient.controllers.BaseController;
import CommonUtils.CommonUtils;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class UnplannedVisitInsertionController extends BaseController {

    @FXML
    private MFXButton btnSubmit;

    @FXML
    private MFXCheckbox cbGroupOrder;

    @FXML
    private Label lblErrorMsg;

    @FXML
    private TextField txtNumOfVisitors;

    @FXML
    private Text txtResult;

    public void cleanup() {
        txtNumOfVisitors.clear();
        cbGroupOrder.setSelected(false);
        lblErrorMsg.setText("");
        txtResult.setText("");
    }

    @FXML
    void OnClickSubmitButton(ActionEvent event) {
        if (!CheckValidInput())
            return;
        int numOfVisitors = GetNumOfVisitors();
        boolean cbSelection = GetCheckBoxSelection();


    }

    private boolean GetCheckBoxSelection() {
        return this.cbGroupOrder.isSelected();
    }

    private int GetNumOfVisitors() {
        return Integer.parseInt(this.txtNumOfVisitors.getText());
    }

    private boolean CheckValidInput() {
        if (!CommonUtils.isAllDigits(this.txtNumOfVisitors.getText())) // case: not a number.
        {
            lblErrorMsg.setText("Invalid number of visitors, not a number!");
            return false;
        }

        int numOfVisitors = GetNumOfVisitors();
        if (numOfVisitors <= 0) // case: less than 1.
        {
            lblErrorMsg.setText("Invalid number of visitors, at least one visitors.");
            return false;
        }

        boolean cbSelection = GetCheckBoxSelection();
        if (cbSelection && numOfVisitors > 15) // case: group order, max 15 visitors.
        {
            lblErrorMsg.setText("Invalid number of visitors, max 15 visitors for group order.");
            return false;
        }

        if (!cbSelection && numOfVisitors > 9) // case: not group, max 9 visitors
        {
            lblErrorMsg.setText("Invalid number of visitors, max 9 visitors for non-group order.");
            return false;
        }

        lblErrorMsg.setText("");
        return true;
    }
}
