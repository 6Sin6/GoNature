package VisitorsControllers;

import CommonClient.Utils;
import CommonClient.controllers.BaseController;
import CommonUtils.InputTextPopup;

import java.util.Objects;

public abstract class GeneralVisitorDashboard extends BaseController {
    protected InputTextPopup onAuthPopup;

    protected void onAuth(String path, String id) {
        String strToPrint = "";
        if (!Utils.isIDValid(id)) {
            strToPrint = "Invalid ID! Try again";
        }
        if (strToPrint.isEmpty() && !id.equals(getUserID())) {
            strToPrint = "Wrong ID! Try again";
        }
        if (strToPrint.isEmpty()) {
            onAuthPopup.setErrorLabel(strToPrint);
            if (!Objects.equals(path, "")) {
                applicationWindowController.setCenterPage(path);
                applicationWindowController.loadMenu(applicationWindowController.getUser());
            }
        } else {
            onAuthPopup.setErrorLabel(strToPrint);
        }
    }

    protected void authenticateWithID(String path) {
        onAuthPopup = new InputTextPopup(new String[]{"Enter ID to Authenticate "}, (inputText) -> this.onAuth(path, inputText[0]), 500, 300, true, false, true);
        onAuthPopup.show(applicationWindowController.getRoot());
    }

    public abstract String getUserID();
}
