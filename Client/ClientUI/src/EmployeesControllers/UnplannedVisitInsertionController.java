package EmployeesControllers;

import CommonClient.controllers.BaseController;
import CommonUtils.CommonUtils;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class UnplannedVisitInsertionController extends BaseController {

    @FXML
    private MFXButton btnSubmit;

    @FXML
    private MFXCheckbox cbGroupOrder;

    @FXML
    private Label lblErrorMsg;

    @FXML
    private MFXTextField txtNumOfVisitors;

    @FXML
    private Text txtResult;

    public void cleanup() {
        txtNumOfVisitors.clear();
        cbGroupOrder.setSelected(false);
        lblErrorMsg.setText("");
        txtResult.setText("");
    }

    @FXML
    void OnClickSubmitButton(ActionEvent event)
    {
        if (!CheckValidInput())
            return;

    }

    private boolean GetCheckBoxSelection()
    {
        return this.cbGroupOrder.isSelected();
    }

    private int GetNumOfVisitors()
    {
        return Integer.parseInt(this.txtNumOfVisitors.getText());
    }

    private boolean CheckValidInput()
    {
        if (!CommonUtils.isAllDigits(this.txtNumOfVisitors.getText())) // case: not a number.
        {
            lblErrorMsg.setText("Invalid number of visitors, not a number!");
            return false;
        }
        lblErrorMsg.setText("");

        int numOfVisitors = GetNumOfVisitors();
        if (numOfVisitors <= 0)
        {
            lblErrorMsg.setText("Invalid number of visitors, ");
            return false;
        }
        else lblErrorMsg.setText("");
        boolean cbSelection = GetCheckBoxSelection();

        return true;
    }

}
