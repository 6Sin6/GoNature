package VisitorsControllers;

import CommonClient.Utils;
import CommonClient.controllers.BaseController;
import CommonUtils.InputTextPopup;

public class GeneralVisitorDashboard extends BaseController {
    protected InputTextPopup onAuthPopup;

    protected void onAuth(String id, String path) {
        String strToPrint = "";
        if (!Utils.isIDValid(id)) {
            strToPrint = "Invalid ID ! Try again";
        }
        if (strToPrint.isEmpty() && !id.equals("316165984")) {
            strToPrint = "Wrong ID ! Try again";
        }
        if (strToPrint.isEmpty()) {
            onAuthPopup.setErrorLabel(strToPrint);
            applicationWindowController.setCenterPage(path);
            applicationWindowController.loadMenu(applicationWindowController.getUser());
        }
    }
}