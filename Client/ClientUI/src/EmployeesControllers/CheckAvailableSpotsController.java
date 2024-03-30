package EmployeesControllers;

import CommonClient.ClientUI;
import CommonClient.controllers.BaseController;
import CommonUtils.CommonUtils;
import CommonUtils.ConfirmationPopup;
import CommonUtils.MessagePopup;
import Entities.Message;
import Entities.OpCodes;
import Entities.ParkBank;
import Entities.ParkEmployee;
import client.ClientCommunicator;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import java.util.ArrayList;

/**
 * The CheckAvailableSpotsController class is responsible for handling the user interactions
 * in the "Check Available Spots" view of the application. This includes checking the availability
 * of parking spots in a selected park and initiating the process of making a new order.
 * The class extends the BaseController class and overrides its cleanup method to reset the UI elements
 * to their default states. It also provides methods to handle the actions triggered by the UI components,
 * such as clicking the "Check Availability" and "Make Order" buttons.
 */
public class CheckAvailableSpotsController extends BaseController {
    /**
     * A list that holds the names of parks. This is observable, meaning UI components
     * can listen and react to its changes.
     */
    private ObservableList<String> list;

    /**
     * The number of available parking spots. This value is updated based on the selected
     * park and current occupancy.
     */
    private Integer availableSpots;

    /**
     * Text for displaying park name.
     */
    @FXML
    private Text txtParkName;

    /**
     * Button to initiate the check for parking spot availability.
     * Uses the JFoenix MFXButton component for a stylized button.
     */
    @FXML
    private MFXButton ctnCheckAvailability;

    /**
     * Text field for displaying miscellaneous information. Specific usage may vary.
     */
    @FXML
    private Text text2;

    /**
     * An additional text field for displaying miscellaneous information. Usage and content
     * depend on specific application requirements.
     */
    @FXML
    private Text text21;

    /**
     * Text field for displaying the current occupancy status of the selected park.
     */
    @FXML
    private Text ParkOccupancyTxt;

    /**
     * ProgressBar indicating the occupancy level of the selected park.
     * Uses the JFoenix MFXProgressBar component for a stylized progress bar.
     */
    @FXML
    private MFXProgressBar progressBar;

    /**
     * Text field for displaying the number of available spots in the selected park.
     */
    @FXML
    private Text availableSpotsTxt;

    /**
     * Button to proceed with making a reservation for a parking spot.
     * Uses the JFoenix MFXButton component.
     */
    @FXML
    private MFXButton MakeOrderBtn;

    /**
     * Label for displaying error messages related to parking spot availability and reservation.
     */
    @FXML
    private Label errorLbl;

    /**
     * Handles the click event on the "Check Availability" button. This method is triggered when the user
     * wants to check the availability of spots in a selected park. It first hides any previously visible
     * UI components related to the park's availability, such as the progress bar and availability text.
     * <p>
     * If no park is selected, it displays an error label. Otherwise, it sends a request to the server to
     * check the available spots in the park identified by the selected item in the {@code parkCmbBox}.
     * Upon receiving a response from the server, it processes the response to update the UI with the
     * availability information, including updating a progress bar to reflect the occupancy level of the
     * park and displaying the number of available spots.
     * <p>
     * Error handling is implemented to display relevant error messages through popups if a database error
     * occurs or if the server response is not as expected.
     * <p>
     * Preconditions:
     * - A park must be selected from the {@code parkCmbBox} for the availability check to proceed.
     * <p>
     * Postconditions:
     * - If available spots are found, the method updates the UI to show the park's occupancy level and
     * the number of available spots. It also makes the "Make Order" button visible to allow proceeding
     * with creating an order.
     * - If an error occurs during the process, appropriate error messages are displayed to the user.
     *
     * @param event The {@link ActionEvent} associated with the button click.
     */
    @FXML
    void OnClickctnCheckAvailability(ActionEvent event) {
        progressBar.setVisible(false);
        ParkOccupancyTxt.setVisible(false);
        MakeOrderBtn.setVisible(false);
        availableSpotsTxt.setVisible(false);
        if (txtParkName.getText().isEmpty()) {
            errorLbl.setVisible(true);
            return;
        }
        String parkID = ParkBank.getUnmodifiableMap().get(txtParkName.getText());
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

    /**
     * Populates the park ComboBox with names of parks. This method retrieves park names from
     * the {@code ParkBank}'s unmodifiable map, which serves as the source of park names and their
     * corresponding IDs. Each park name is added to an ArrayList, which is then converted to an
     * {@link ObservableList} to ensure UI components can react to the data changes.
     * <p>
     * The {@code parkCmbBox} items are set using this observable list, making the park names available
     * for selection in the ComboBox. This method is intended to be called during the initialization
     * phase of the view to ensure the park selection ComboBox is fully populated before user interaction.
     * <p>
     * Preconditions:
     * - The {@code ParkBank} must be initialized and contain a mapping of park names to park IDs.
     * <p>
     * Postconditions:
     * - The {@code parkCmbBox} is populated with the names of parks, allowing the user to select a park
     * from a dropdown list.
     */
    private void setParkText() {
        txtParkName.setText(ParkBank.getParkNameByID(((ParkEmployee) applicationWindowController.getUser()).getPark().getParkID()));
    }

    /**
     * Initializes the controller class. This method is automatically called after the FXML file has been loaded.
     * It sets up the initial state of the UI components, specifically populating the park selection ComboBox
     * and resetting any UI elements to their default state through the {@code cleanup} method.
     * <p>
     * The {@code setParkText} method is called to populate the ComboBox with park names, ensuring the user
     * can select from the available parks as soon as the UI is displayed. The {@code cleanup} method is invoked
     * to clear any residual data or states that may persist from previous uses of the UI, such as error messages
     * or previously selected items, ensuring a clean state for user interaction.
     */
    public void start() {
        cleanup();
        setParkText();
    }


    /**
     * Handles the action triggered by clicking the "Make Order" button. This method opens a new popup window
     * for submitting a spontaneous order, passing along the selected park's name and the number of available spots
     * to the popup's controller. It is designed to facilitate the process of making a new order based on the
     * user's selection and the current park availability information.
     * <p>
     * The method initializes a {@link MessagePopup} with the FXML layout for the spontaneous order submission form,
     * retrieves the controller for the popup, and sets necessary data and controllers on it. This includes passing
     * the {@code ApplicationWindowController} for managing application-wide operations and the {@code MessagePopup}
     * instance for allowing the popup controller to manage its own window. The {@code start} method of the popup
     * controller is then called with the necessary parameters to initialize its view.
     *
     * @param event The {@link ActionEvent} triggered by clicking the button.
     *              <p>
     *              Exceptions:
     *              - This method catches and logs any exceptions that occur during the process of opening the popup or setting up
     *              its controller. This ensures that the application does not crash and allows for debugging potential issues
     *              in the order submission process.
     */
    @FXML
    void OnClickMakeOrderBtn(ActionEvent event) {
        try {
            MessagePopup msg = new MessagePopup("/EmployeesUI/SpontaneousOrderSubmit.fxml", 0, 0, true, false);
            SpontaneousOrderSubmitController controller = (SpontaneousOrderSubmitController) msg.getController();
            controller.setApplicationWindowController(applicationWindowController);
            msg.show(applicationWindowController.getRoot());

            controller.setMessagePopup(msg);
            controller.start(txtParkName.getText(), availableSpots);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Resets the UI elements to their default states. This method is called to clear any displayed information
     * and hide UI components that are not relevant until certain actions are performed. Specifically, it hides
     * the progress bar, park occupancy text, and the "Make Order" button, which are only displayed after checking
     * for park availability. It also clears any error messages and resets the park selection ComboBox.
     * <p>
     * This cleanup method is typically invoked when the view is being prepared for the user to start a new action,
     * or when the user navigates away from the current view, ensuring that the next time the view is presented, it
     * starts in a clean state.
     * <p>
     * Postconditions:
     * - The progress bar, park occupancy text, and "Make Order" button are not visible.
     * - The error label is hidden and the available spots text is cleared.
     * - The park selection ComboBox is reset to an empty selection.
     */
    @Override
    public void cleanup() {
        progressBar.setVisible(false);
        ParkOccupancyTxt.setVisible(false);
        MakeOrderBtn.setVisible(false);
        errorLbl.setVisible(false);
        availableSpotsTxt.setText("");
        txtParkName.setText("");
    }

}
