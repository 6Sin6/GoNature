package CommonClient.controllers;

import CommonClient.ClientUI;
import CommonClient.Utils;
import CommonUtils.InputTextPopup;
import Entities.Message;
import Entities.OpCodes;
import Entities.User;
import client.ClientCommunicator;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;

import javax.naming.CommunicationException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomePageController extends BaseController implements Initializable {
    @FXML
    private MFXButton bookBtn;

    @FXML
    private ImageView centerImg;

    @FXML
    private ImageView img1;

    @FXML
    private ImageView img2;

    @FXML
    private MFXButton signInBtn;

    private InputTextPopup onAuthPopup;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        centerImg.toFront();
        centerImg.setEffect(new DropShadow());
        img1.setEffect(new DropShadow());
        img2.setEffect(new DropShadow());
    }

    public void onButtonClicked() {
        if (applicationWindowController.getUser() != null) {
            applicationWindowController.logout();
            return;
        }
        applicationWindowController.setCenterPage("/CommonClient/gui/LoginPage.fxml");
    }

    private void onAuth(String id, String path) throws CommunicationException {
        if (!Utils.isIDValid(id)) {
            onAuthPopup.setErrorLabel("Invalid ID Format! Try again");
            return;
        }

        Message message = new Message(OpCodes.OP_GET_USER_BY_ID, "", id);
        ClientUI.client.accept(message);
        Message response = ClientCommunicator.msg;
        OpCodes returnOpCode = response.getMsgOpcode();

        // Checking if the response from the server is inappropriate.
        if (returnOpCode != OpCodes.OP_GET_USER_BY_ID) {
            throw new CommunicationException("Response is inappropriate from server");
        }

        User user = (User) response.getMsgData();
        if (user == null) {
            onAuthPopup.setErrorLabel("Invalid ID! Try again");
            return;
        }

        onAuthPopup.setErrorLabel("");
        applicationWindowController.setCenterPage(path);
        applicationWindowController.loadMenu(user);
    }

    public void handleExistingOrder() {
        onAuthPopup = new InputTextPopup("Enter ID to Authenticate ", (inputText) -> {
            try {
                this.onAuth(inputText, "/VisitorsUI/ActiveOrdersPage.fxml");
            } catch (CommunicationException e) {
                e.printStackTrace();
            }
        }, 500, 300, true, true, true);
        onAuthPopup.show(applicationWindowController.getRoot());
    }

    public void onBookButtonClicked() {
        if (applicationWindowController.getUser() == null) {
            applicationWindowController.setCenterPage("/CommonClient/gui/LoginPage.fxml");
            return;
        }
        applicationWindowController.loadDashboardPage(applicationWindowController.getUser().getRole());
    }
}
