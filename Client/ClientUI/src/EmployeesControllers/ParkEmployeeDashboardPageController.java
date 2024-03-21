package EmployeesControllers;

import CommonUtils.InputTextPopup;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class ParkEmployeeDashboardPageController extends GeneralEmployeeDashboard {

    @FXML
    public void OnClickAvailableSpotButton(ActionEvent event)
    {
        applicationWindowController.loadEmployeesPage("CheckAvailableSpotsPage");
    }

    @FXML
    public void OnClickGenerateBillButton(ActionEvent event)
    {
        applicationWindowController.loadEmployeesPage("GenerateBillPage");
    }


    @FXML
    void OnClickExitButton(ActionEvent event)
    {
        popup = new InputTextPopup(new String[]{"Enter Order ID"}, super::onSubmit, 0, 0, true, true, false);
        popup.show(applicationWindowController.getRoot());
    }

}
