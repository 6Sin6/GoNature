package VisitorsControllers;

import CommonClient.ClientUI;
import CommonClient.Utils;
import CommonClient.controllers.BaseController;
import CommonUtils.InputTextPopup;
import Entities.Message;
import Entities.OpCodes;
import client.ClientCommunicator;

import java.util.ArrayList;
import java.util.Objects;

public abstract class GeneralVisitorDashboard extends BaseController {
    protected InputTextPopup onAuthPopup;

    protected void onAuth(String path, String id) {
        String strToPrint = "";
        if (!Utils.isIDValid(id)) {
            strToPrint = "Invalid ID! Try again";
        }
        if (strToPrint.isEmpty() && !id.equals(getUserID())) {
            strToPrint = "Invalid ID Format! Try again";
        }
        if (strToPrint.isEmpty()) {
            onAuthPopup.setErrorLabel(strToPrint);
            if (!Objects.equals(path, "")) {
                applicationWindowController.setCenterPage(path);
                applicationWindowController.loadMenu(applicationWindowController.getUser());
                if (Objects.equals(path, "/VisitorsUI/ActiveOrdersPage.fxml")) {
                    Message send = new Message(OpCodes.OP_GET_VISITOR_ORDERS, applicationWindowController.getUser().getUsername(), applicationWindowController.getUser());
                    ClientUI.client.accept(send);
                    if (ClientCommunicator.msg.getMsgOpcode() == OpCodes.OP_GET_VISITOR_ORDERS) {
                            Object controller = applicationWindowController.currentActiveController;
                            if (controller instanceof ActiveOrdersPageController)
                                ((ActiveOrdersPageController) controller).populateTable((ArrayList) (ClientCommunicator.msg.getMsgData()));

                    }
                }
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
