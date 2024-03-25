package EmployeesControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class SupportRepresentativeDashboardPageController extends GeneralEmployeeDashboard {

    @FXML
    public void OnClickRegisterGuideButton(ActionEvent ignoredEvent)
    {
        applicationWindowController.loadEmployeesPage("RegisterGroupGuidePage");
    }
}
