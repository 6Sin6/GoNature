package VisitorsControllers;

import CommonClient.ClientUI;
import CommonClient.controllers.BaseController;
import CommonUtils.ConfirmationPopup;
import CommonUtils.CommonUtils;
import Entities.*;
import client.ClientCommunicator;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.naming.CommunicationException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.regex.Pattern;


public class VisitorOrderVisitationPageController extends BaseController implements Initializable {

    ObservableList<String> list;

    @FXML
    private Text Header;

    @FXML
    private MFXButton btnCreateOrder;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Pane pane;

    @FXML
    private MFXLegacyComboBox<String> parkCmbBox;

    @FXML
    private Separator sepOrder;

    @FXML
    private StackPane stackPane;

    @FXML
    private MFXLegacyComboBox<String> timeOfVisitCmbBox;

    @FXML
    private MFXTextField txtEmail;

    @FXML
    private MFXTextField txtFirstName;

    @FXML
    private MFXTextField txtLastName;

    @FXML
    private MFXTextField txtNumOfVisitors;

    @FXML
    private MFXTextField txtPhone;


    private void setParkCmbBox() {
        ArrayList<String> al = new ArrayList<String>();
        for (String key : ParkBank.getUnmodifiableMap().keySet()) {
            al.add(key);
        }
        list = FXCollections.observableArrayList(al);
        parkCmbBox.setItems(list);
    }

    private void setTimeOfVisitCmbBox() {
        ArrayList<String> al = new ArrayList<String>();
        al.add("08:00");
        al.add("12:00");
        al.add("16:00");
        list = FXCollections.observableArrayList(al);
        timeOfVisitCmbBox.setItems(list);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setParkCmbBox();
        setTimeOfVisitCmbBox();
    }

    @FXML
    void OnClickCreateOrderButton(ActionEvent event) throws CommunicationException {
        if (isEmptyFields()) {
            System.out.println("one or more details are empty ");
            return;
        }
        if (!CommonUtils.isValidPhone(txtPhone.getText())) {
            System.out.println("The phone number is not valid ");
            return;
        }
        if (!(CommonUtils.containsOnlyLetters(txtFirstName.getText())) || !CommonUtils.containsOnlyLetters(txtLastName.getText())) {
            System.out.println("First or last name  should contains only letters");
            return;
        }
        if (!CommonUtils.isEmailAddressValid(txtEmail.getText())) {
            System.out.println("Email addres is not valid");
            return;
        }
        if ((CommonUtils.convertStringToInt(txtNumOfVisitors.getText()) <= 0)) {
            System.out.println("The number isn't valid");
            return;
        }
        if (!(applicationWindowController.getUser() instanceof SingleVisitor)) {
            System.out.println("The user isn't visitor");
            return;
        }

        SingleVisitor visitor = (SingleVisitor) applicationWindowController.getUser();
        Timestamp timeOfVisit = convertStringToTimestamp(datePicker.getValue().toString(), timeOfVisitCmbBox.getValue());
        Order order = new Order(visitor.getID(), ParkBank.getUnmodifiableMap().get(parkCmbBox.getValue()), timeOfVisit, txtEmail.getText(), txtPhone.getText(), null, timeOfVisit, timeOfVisit, null, OrderType.ORD_TYPE_SINGLE, (CommonUtils.convertStringToInt(txtNumOfVisitors.getText())));
        Object msg = new Message(OpCodes.OP_CREATE_NEW_VISITATION, visitor.getUsername(), order);
        ClientUI.client.accept(msg);
        Message respondMsg = ClientCommunicator.msg;
        OpCodes returnOpCode = respondMsg.getMsgOpcode();
        if (returnOpCode != OpCodes.OP_CREATE_NEW_VISITATION) {
            throw new CommunicationException("Respond not appropriate from server");
        }
        if (respondMsg.getMsgData() instanceof Order) {
            if (((Order) respondMsg.getMsgData()).getOrderID() != null) {
                Order cnfrmorder = (Order) respondMsg.getMsgData();
                String strForPopup = "The order " + cnfrmorder.getOrderID() + " has been created successfully";
                ConfirmationPopup confirmPopup = new ConfirmationPopup(strForPopup, () ->
                {
                    applicationWindowController.loadDashboardPage(applicationWindowController.getUser().getRole());
                    applicationWindowController.loadMenu(applicationWindowController.getUser());
                }
                        , 300, 150, false, "OK", false);
                confirmPopup.show(applicationWindowController.getRoot());
            } else {
                String strForPopup = "The park is Full Do you want to enter for waitlist?";
                ConfirmationPopup confirmPopup;
                confirmPopup = new ConfirmationPopup(strForPopup, () ->
                {
                    applicationWindowController.setCenterPage("/VisitorsUI/WaitListPage");
                    applicationWindowController.loadMenu(applicationWindowController.getUser());
                    ;
                }, () -> {
                    applicationWindowController.loadDashboardPage(applicationWindowController.getUser().getRole());
                    applicationWindowController.loadMenu(applicationWindowController.getUser());
                },
                        300, 150, false, "Yes", "No", false);
                confirmPopup.show(applicationWindowController.getRoot());
                return;
            }
        } else {
            System.out.println("The order is not created");
        }


    }

    private boolean isEmptyFields() {
        if (txtFirstName.getText().isEmpty() || txtLastName.getText().isEmpty() || isParkEmpty() || !isTextFieldInteger(txtNumOfVisitors)
                || isDateEmpty() || isTimeEmpty() || txtPhone.getText().isEmpty() || txtEmail.getText().isEmpty()) {
            System.out.println("One or more details are empty");
            return true;
        }
        return false;
    }

    private boolean isDateEmpty() {
        if (datePicker.getValue() == null) {
            return true;
        }
        return false;
    }

    private boolean isParkEmpty() {
        if (parkCmbBox.getValue() == null) {
            return true;
        }
        return false;

    }

    private boolean isTimeEmpty() {
        if (timeOfVisitCmbBox.getValue() == null) {
            return true;
        }
        return false;

    }

    private boolean isTextFieldInteger(TextField textField) {
        try {
            // Try parsing the content of the TextField as an integer
            Integer.parseInt(textField.getText());
            return true; // If successful, it's an integer
        } catch (NumberFormatException e) {
            return false; // If an exception occurs, it's not an integer
        }
    }


    private Timestamp convertStringToTimestamp(String date, String time) {
        // Combine Date and Time Strings
        String dateTimeString = date + "T" + time;

        // Define the formatter for LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        // Parse the String to LocalDateTime
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);

        // Convert LocalDateTime to java.sql.Timestamp
        Timestamp timestamp = Timestamp.valueOf(dateTime);

        return timestamp;
    }
}
