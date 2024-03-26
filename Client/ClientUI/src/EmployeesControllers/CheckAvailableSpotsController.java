package EmployeesControllers;

import CommonClient.ClientUI;
import CommonClient.controllers.BaseController;
import CommonUtils.ConfirmationPopup;
import CommonUtils.MessagePopup;
import CommonUtils.*;
import Entities.Message;
import Entities.OpCodes;
import Entities.ParkBank;
import client.ClientCommunicator;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CheckAvailableSpotsController extends BaseController implements Initializable {
    ObservableList<String> list;
    private Integer availableSpots;

    @FXML
    private MFXLegacyComboBox<String> parkCmbBox;

    @FXML
    private MFXButton ctnCheckAvailability;

    @FXML
    private Text text2;

    @FXML
    private Text text21;

    @FXML
    private Text ParkOccupancyTxt;

    @FXML
    private MFXProgressBar progressBar;

    @FXML
    private Text availableSpotsTxt;

    @FXML
    private MFXButton MakeOrderBtn;

    @FXML
    private Label errorLbl;

    @FXML
    void OnClickctnCheckAvailability(ActionEvent event) {
        progressBar.setVisible(false);
        ParkOccupancyTxt.setVisible(false);
        MakeOrderBtn.setVisible(false);
        availableSpotsTxt.setVisible(false);
        if (parkCmbBox.getValue().equals("")) {
            errorLbl.setVisible(true);
            return;
        }
        String parkID = ParkBank.getUnmodifiableMap().get(parkCmbBox.getValue());
        errorLbl.setVisible(false);
        Message msgToServer = new Message(OpCodes.OP_CHECK_AVAILABLE_SPOT, applicationWindowController.getUser().getUsername(), parkID);
        ClientUI.client.accept(msgToServer);
        Object answer = ClientCommunicator.msg;
        Message msgFromServer = (Message) answer;
        OpCodes returnOpCode = msgFromServer.getMsgOpcode();

        if (returnOpCode == OpCodes.OP_DB_ERR) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        // Checking if the response from the server is inappropriate.
        if (returnOpCode != OpCodes.OP_CHECK_AVAILABLE_SPOT) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        if (!(msgFromServer.getMsgData() instanceof ArrayList)) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }


        ArrayList<Integer> msgData = (ArrayList<Integer>) msgFromServer.getMsgData();
        Integer parkCapacity = msgData.get(1);
        availableSpots = msgData.get(0);
        progressBar.setProgress((double) (parkCapacity - availableSpots) / parkCapacity);
        availableSpotsTxt.setText("Available spots right now : " + availableSpots);
        availableSpotsTxt.setVisible(true);
        progressBar.setVisible(true);
        ParkOccupancyTxt.setVisible(true);
        if (availableSpots > 0) {
            MakeOrderBtn.setVisible(true);
        }

    }

    private void setParkCmbBox() {
        ArrayList<String> al = new ArrayList<String>();
        for (String key : ParkBank.getUnmodifiableMap().keySet()) {
            al.add(key);
        }
        list = FXCollections.observableArrayList(al);
        parkCmbBox.setItems(list);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setParkCmbBox();
        cleanup();
    }

    @FXML
    void OnClickMakeOrderBtn(ActionEvent event) {
        try {
            MessagePopup msg = new MessagePopup("/EmployeesUI/SpontaneousOrderSubmit.fxml", 0, 0, true, false);
            SpontaneousOrderSubmitController controller = (SpontaneousOrderSubmitController) msg.getController();
            controller.setApplicationWindowController(applicationWindowController);
            msg.show(applicationWindowController.getRoot());

            controller.setMessagePopup(msg);
            controller.start(parkCmbBox.getValue(), availableSpots);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cleanup() {
        progressBar.setVisible(false);
        ParkOccupancyTxt.setVisible(false);
        MakeOrderBtn.setVisible(false);
        errorLbl.setVisible(false);
        availableSpotsTxt.setText("");
        parkCmbBox.setValue("");
    }

}
