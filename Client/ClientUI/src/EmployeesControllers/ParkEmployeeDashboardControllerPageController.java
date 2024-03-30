package EmployeesControllers;

import javafx.event.ActionEvent;

/**
 * This class represents the controller for the park employee dashboard page.
 * It extends the {@link GeneralEmployeeDashboardController} class and provides specific functionality
 * related to handling user interactions and events on the park employee dashboard.
 */
public class ParkEmployeeDashboardControllerPageController extends GeneralEmployeeDashboardController {

    /**
     * Handles the action event triggered by clicking the "UpdateExitTime" button.
     * This method calls the superclass method {@code OnClickExitButton} with a null argument,
     * effectively simulating a click on the exit button to update the exit time.
     *
     * @param ignoredEvent The ActionEvent triggered by clicking the "UpdateExitTime" button.
     */
    public void OnClickUpdateExitTime(ActionEvent ignoredEvent) {
        super.OnClickExitButton(null);
    }
}
