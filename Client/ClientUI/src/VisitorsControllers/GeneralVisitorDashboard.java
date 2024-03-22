package VisitorsControllers;

import CommonClient.controllers.BaseController;

import java.util.Objects;

public abstract class GeneralVisitorDashboard extends BaseController {

    protected void routeToPage(String path) {
        applicationWindowController.setCenterPage(path);
        applicationWindowController.loadMenu(applicationWindowController.getUser());
        applicationWindowController.setCenterPage(path);
        applicationWindowController.loadMenu(applicationWindowController.getUser());
        if (Objects.equals(path, "/VisitorsUI/ActiveOrdersPage.fxml")) {
            Object controller = applicationWindowController.getCurrentActiveController();
            if (controller instanceof ActiveOrdersPageController) {
                ((ActiveOrdersPageController) controller).start();
            }
        }
    }

    public abstract String getUserID();
}
