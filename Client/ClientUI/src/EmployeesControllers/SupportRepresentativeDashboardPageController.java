package EmployeesControllers;

import CommonUtils.InputTextPopup;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class SupportRepresentativeDashboardPageController extends GeneralEmployeeDashboard {

    @FXML
    public void OnClickAvailableSpotButton(ActionEvent ignoredEvent)
    {
        applicationWindowController.loadEmployeesPage("CheckAvailableSpotsPage");
    }

    @FXML
    public void OnClickGenerateBillButton(ActionEvent ignoredEvent)
    {
        applicationWindowController.loadEmployeesPage("GenerateBillPage");
    }

    @FXML
    public void OnClickRegisterGuideButton(ActionEvent ignoredEvent)
    {
        applicationWindowController.loadEmployeesPage("RegisterGroupGuidePage");
    }

    @FXML
    void OnClickExitButton(ActionEvent ignoredEvent)
    {
        popup = new InputTextPopup(new String[]{"Enter Order ID"}, this::onSubmit, 0, 0, true, true, false);
        popup.show(applicationWindowController.getRoot());
    }
}
