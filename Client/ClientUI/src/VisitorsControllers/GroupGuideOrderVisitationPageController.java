package VisitorsControllers;


import CommonClient.ClientUI;
import CommonClient.controllers.BaseController;
import CommonUtils.CommonUtils;
import CommonUtils.ConfirmationPopup;
import CommonUtils.MessagePopup;
import Entities.*;
import client.ClientCommunicator;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javax.naming.CommunicationException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class GroupGuideOrderVisitationPageController extends BaseController implements Initializable {
    /**
     * ObservableList to hold data for combo boxes or other UI components.
     */
    ObservableList<String> list;

    /**
     * Reference to the TextField node for entering email.
     */
    @FXML
    private TextField txtEmail;

    /**
     * Reference to the TextField node for entering first name.
     */
    @FXML
    private TextField txtFirstName;

    /**
     * Reference to the TextField node for entering last name.
     */
    @FXML
    private TextField txtLastName;

    /**
     * Reference to the TextField node for entering phone number.
     */
    @FXML
    private TextField txtPhone;

    /**
     * Reference to the DatePicker node for selecting date.
     */
    @FXML
    private DatePicker datePicker;

    /**
     * Reference to the MFXLegacyComboBox node for selecting the number of visitors.
     */
    @FXML
    private MFXLegacyComboBox<String> numOfVisitorsCmbBox;

    /**
     * Reference to the MFXLegacyComboBox node for selecting the park.
     */
    @FXML
    private MFXLegacyComboBox<String> parkCmbBox;

    /**
     * Reference to the MFXLegacyComboBox node for selecting the time of visit.
     */
    @FXML
    private MFXLegacyComboBox<String> timeOfVisitCmbBox;

    /**
     * Reference to the Label node for displaying information.
     */
    @FXML
    private Label label;


    /**
     * Resets the input fields and selections in the UI to their default state.
     * This method clears the text fields for email, first name, last name, and phone number,
     * clears selections in combo boxes for the number of visitors, park, and time of visit,
     * and resets the date picker to null.
     */
    public void cleanup() {
        txtEmail.clear();
        txtFirstName.clear();
        txtLastName.clear();
        txtPhone.clear();
        numOfVisitorsCmbBox.getSelectionModel().clearSelection();
        parkCmbBox.getSelectionModel().clearSelection();
        timeOfVisitCmbBox.getSelectionModel().clearSelection();
        numOfVisitorsCmbBox.setValue(null);
        parkCmbBox.setValue(null);
        timeOfVisitCmbBox.setValue(null);
        datePicker.setValue(null);
    }


    /**
     * Populates the park selection combo box with available parks.
     * The parks are retrieved from a static data source.
     */
    private void setParkCmbBox() {
        ArrayList<String> al = new ArrayList<String>();
        for (String key : ParkBank.getUnmodifiableMap().keySet()) {
            al.add(key);
        }
        list = FXCollections.observableArrayList(al);
        parkCmbBox.setItems(list);
    }


    /**
     * Populates the time of visit combo box with hourly time slots.
     * Time slots range from 08:00 to 19:00.
     */
    private void setTimeOfVisitCmbBox() {
        ArrayList<String> al = new ArrayList<String>();
        for (int i = 8; i <= 19; i++) {
            if (i < 10) {
                al.add("0" + i + ":00");
            } else {
                al.add("" + i + ":00");
            }
        }
        list = FXCollections.observableArrayList(al);
        timeOfVisitCmbBox.setItems(list);
    }

    /**
     * Disables date selection for past dates and dates more than one year into the future
     * in the {@link DatePicker}.
     */
    private void setDatePicker() {
        datePicker.setDayCellFactory(picker -> new DateCell() {
            LocalDate maxDate = LocalDate.now().plusYears(1); // Setting maximum date to one year from now

            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(LocalDate.now()) < 0 || date.compareTo(maxDate) > 0);
            }
        });
    }


    /**
     * Sets up the options for the number of visitors combo box.
     * This method populates the combo box with values ranging from 2 to 15.
     * It creates an observable list from an array list containing string representations of numbers,
     * then sets this list as the items of the combo box.
     */
    private void setNumOfVisitorsCmbBox() {
        ArrayList<String> al = new ArrayList<String>();
        for (int i = 2; i <= 15; i++) {
            al.add(String.valueOf(i));
        }
        list = FXCollections.observableArrayList(al);
        numOfVisitorsCmbBox.setItems(list);
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded. It initializes the form components
     * and sets up any necessary data bindings or event handlers.
     *
     * @param location  The location used to resolve relative paths for the root object, or null if unknown.
     * @param resources The resources used to localize the root object, or null if not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setParkCmbBox();
        setTimeOfVisitCmbBox();
        setDatePicker();
        setNumOfVisitorsCmbBox();

    }


    /**
     * Handles the action event when the "Create Order" button is clicked.
     * This method validates the input fields, creates a new order based on the provided information,
     * sends a message to the server to create a new visitation order, and handles the response accordingly.
     * If successful, it prompts the user to pay now or later. If the order already exists or the park is at full capacity,
     * it provides options to view alternative times or sign up for the waitlist.
     *
     * @param event The ActionEvent representing the user's click on the "Create Order" button.
     * @throws CommunicationException If an error occurs during communication with the server.
     */
    @FXML
    void OnClickCreateOrderButton(ActionEvent event) throws CommunicationException {
        if (!validateFields()) {
            return;
        }
        if (!(applicationWindowController.getUser() instanceof VisitorGroupGuide)) {
            System.out.println("The user isn't visitor");
            return;
        }
        label.setText("");
        VisitorGroupGuide guide = (VisitorGroupGuide) applicationWindowController.getUser();
        Timestamp timeOfVisit = CommonUtils.convertStringToTimestamp(datePicker.getValue().toString(), timeOfVisitCmbBox.getValue());
        Order order = new Order(guide.getID(), ParkBank.getUnmodifiableMap().get(parkCmbBox.getValue()), timeOfVisit, txtEmail.getText(), txtPhone.getText(), null, timeOfVisit, null, null, OrderType.ORD_TYPE_GROUP, (CommonUtils.convertStringToInt(numOfVisitorsCmbBox.getValue())));
        Object msg = new Message(OpCodes.OP_CREATE_NEW_VISITATION, guide.getUsername(), order);
        ClientUI.client.accept(msg);
        Message respondMsg = ClientCommunicator.msg;
        OpCodes returnOpCode = respondMsg.getMsgOpcode();
        if (returnOpCode == OpCodes.OP_DB_ERR) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        if (returnOpCode != OpCodes.OP_CREATE_NEW_VISITATION && returnOpCode != OpCodes.OP_NO_AVAILABLE_SPOT && returnOpCode != OpCodes.OP_ORDER_ALREADY_EXIST) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        if (returnOpCode == OpCodes.OP_CREATE_NEW_VISITATION && !(respondMsg.getMsgData() instanceof Order)) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        if (returnOpCode == OpCodes.OP_CREATE_NEW_VISITATION) {
            Order cnfrmorder = (Order) respondMsg.getMsgData();
            String strForPopup = "The order " + cnfrmorder.getOrderID() + " has been created successfully! Would you like to pay now and get discount or pay later ?";
            ConfirmationPopup confirmPopup = new ConfirmationPopup(strForPopup, () -> handleBillPresentation(cnfrmorder), () ->
            {
                applicationWindowController.loadDashboardPage(applicationWindowController.getUser().getRole());
                applicationWindowController.loadMenu(applicationWindowController.getUser());
                clearFields();
            }
                    , 950, 500, false, "Pay now!", "Later", false);
            confirmPopup.show(applicationWindowController.getRoot());
        } else if (returnOpCode == OpCodes.OP_ORDER_ALREADY_EXIST) {
            String strForPopup = "You already have an order with these details...\nWould you like to view alternatives times?";
            String fullName = "" + txtFirstName.getText() + " " + txtLastName.getText();
            ConfirmationPopup confirmPopup = new ConfirmationPopup(strForPopup, () ->
            {
                applicationWindowController.loadVisitorsPage("AlternativeTimesTable");
                Object controller = applicationWindowController.getCurrentActiveController();
                if (controller instanceof AlternativeTimesTableController) {
                    ((AlternativeTimesTableController) controller).start(order, fullName);
                }
            }, () ->
            {
                applicationWindowController.loadDashboardPage(applicationWindowController.getUser().getRole());
                applicationWindowController.loadMenu(applicationWindowController.getUser());
            }
                    , 600, 300, false, "Alternative Time", "Dashboard", false);
            confirmPopup.show(applicationWindowController.getRoot());
        } else {
            String strForPopup = "The park is at full capacity. Would you like to signup to the waitlist?";
            ConfirmationPopup confirmPopup;
            String fullName = "" + txtFirstName.getText() + " " + txtLastName.getText();
            confirmPopup = new ConfirmationPopup(strForPopup, () ->
            {
                applicationWindowController.loadVisitorsPage("WaitListPage");
                Object controller = applicationWindowController.getCurrentActiveController();
                if (controller instanceof WaitListPageController) {
                    ((WaitListPageController) controller).setFields(order, fullName);
                }
                clearFields();
            }, () -> {
                applicationWindowController.loadVisitorsPage("AlternativeTimesTable");
                Object controller = applicationWindowController.getCurrentActiveController();
                if (controller instanceof AlternativeTimesTableController) {
                    ((AlternativeTimesTableController) controller).start(order, fullName);
                }
            },
                    800, 400, false, "WaitListPage", "AlternativeTimesTable", false);
            confirmPopup.show(applicationWindowController.getRoot());
        }

    }

    /**
     * Validates the input fields of the form. Checks for empty fields, valid email, phone number,
     * and the number of visitors.
     *
     * @return true if all inputs are valid, false otherwise.
     */
    private boolean validateFields() {
        if (CommonUtils.anyStringEmpty(txtFirstName.getText(), txtLastName.getText(), txtPhone.getText(), txtEmail.getText(), numOfVisitorsCmbBox.getValue()) || parkCmbBox.getValue() == null || datePicker.getValue() == null || timeOfVisitCmbBox.getValue() == null) {
            label.setText("One or more fileds are empty.");
            return false;
        }
        if (!CommonUtils.isValidPhone(txtPhone.getText())) {
            label.setText("Invalid phone. Please check your input.");
            return false;
        }
        if (!CommonUtils.isValidName(txtFirstName.getText()) || !CommonUtils.isValidName(txtLastName.getText())) {
            label.setText("Validation failed. Please check your input.");
            return false;
        }
        if (!CommonUtils.isEmailAddressValid(txtEmail.getText())) {
            label.setText("Invalid email. Please check your input.");
            return false;
        }
        if (CommonClient.Utils.isOrderTimeValid(datePicker.getValue().toString(), timeOfVisitCmbBox.getValue().toString())) {
            label.setText("Invalid OrderTime. Can't make the order for near 24 hours.");
            return false;
        }
        if (CommonUtils.convertStringToInt(numOfVisitorsCmbBox.getValue()) <= 0) {
            label.setText("Invalid number of visitors. Please check your input.");
            return false;
        }
        return true;
    }

    /**
     * Clears all user input fields.
     */
    private void clearFields() {
        // Clear text fields
        txtEmail.clear();
        txtFirstName.clear();
        txtLastName.clear();
        txtPhone.clear();
        label.setText("");
        // Reset combo boxes
        numOfVisitorsCmbBox.getSelectionModel().clearSelection();
        parkCmbBox.getSelectionModel().clearSelection();
        timeOfVisitCmbBox.getSelectionModel().clearSelection();
        numOfVisitorsCmbBox.setValue(null); // Use this if the combo box text does not clear with clearSelection()
        parkCmbBox.setValue(null);
        timeOfVisitCmbBox.setValue(null);

        // Reset the date picker
        datePicker.setValue(null);
    }


    /**
     * Handles the presentation of a bill for the given order.
     * This method displays a message popup containing the bill generation UI.
     * It sets up the controller for generating the bill and starts the process.
     *
     * @param order The order for which the bill needs to be generated.
     */
    private void handleBillPresentation(Order order) {
        try {
            MessagePopup msg = new MessagePopup("/VisitorsUI/GenerateBillForGroupGuide.fxml", 0, 0, true, false);
            GenerateBillForGroupGuideController controller = (GenerateBillForGroupGuideController) msg.getController();
            controller.setApplicationWindowController(applicationWindowController);
            msg.show(applicationWindowController.getRoot());

            controller.setMessagePopup(msg);
            controller.start(order, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
