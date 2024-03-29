package EmployeesControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class SupportRepresentativeDashboardControllerPageController extends GeneralEmployeeDashboardController {

    @FXML
    public void OnClickRegisterGuideButton(ActionEvent ignoredEvent) {
        applicationWindowController.loadEmployeesPage("RegisterGroupGuidePage");
    }

    public void OnClickUpdateExitTime(ActionEvent event) {
        super.OnClickExitButton(null);
    }
}
