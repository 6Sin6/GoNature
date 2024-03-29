package VisitorsControllers;

import CommonClient.ClientUI;
import CommonClient.controllers.BaseController;
import CommonUtils.CommonUtils;
import CommonUtils.ConfirmationPopup;
import CommonUtils.MessagePopup;
import Entities.Message;
import Entities.OpCodes;
import Entities.Order;
import Entities.SingleVisitor;
import client.ClientCommunicator;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static CommonUtils.CommonUtils.parseVisitDate;
import static CommonUtils.CommonUtils.parseVisitTime;

public class AlternativeTimesTableController extends BaseController implements Initializable {

    /**
     * Button for choosing an option. The specific functionality of this button
     * is determined by the context within the application UI.
     */
    @FXML
    private MFXButton LblChooseBtn;

    /**
     * Pane that handles order-related actions. This could be used for displaying
     * controls or information specific to processing orders.
     */
    @FXML
    private Pane bntHandleOrder;

    /**
     * Table column representing the date associated with each order.
     * This column is used within a TableView to display dates.
     */
    @FXML
    private TableColumn<Map, String> colDate;

    /**
     * Table column representing the time associated with each order.
     * This column is used within a TableView to display times.
     */
    @FXML
    private TableColumn<Map, String> colTime;

    /**
     * Label used for displaying status messages to the user. This could include
     * error messages, success notifications, or other relevant status updates.
     */
    @FXML
    private Label lblStatusMsg;

    /**
     * TableView for displaying orders. Each order is represented as a Map with
     * String keys and values, allowing for flexible data representation.
     */
    @FXML
    private TableView<Map<String, String>> tableOrders;

    /**
     * Index of the currently selected row in the orders table.
     * This is used to identify and manipulate specific orders within the UI.
     */
    private int rowIndex;

    /**
     * Label used for displaying error messages to the user. This is typically
     * used to provide feedback in case of invalid actions or inputs.
     */
    @FXML
    private Label lblError;

    /**
     * List of timestamps, potentially used for tracking timing information related
     * to orders or user actions within the application.
     */
    private ArrayList<Timestamp> list = new ArrayList<>();

    /**
     * Temporary storage for an Order object. This could be used for processing or
     * manipulation of an order before finalizing actions such as submission or editing.
     */
    private Order tempOrder;

    /**
     * Temporary storage for a full name. This could be associated with a user or
     * an entity related to the current context within the application.
     */
    private String tempfullName;


    /**
     * Resets the UI and internal state related to order processing or display.
     * This method is typically called to clear any error messages and reset the selection index,
     * possibly in preparation for a new operation or after completing an existing one.
     * Specifically, it performs the following actions:
     * <ul>
     *     <li>Resets the {@code rowIndex} to -1, indicating no row is currently selected in the table.</li>
     *     <li>Clears any text from the {@code lblError} label, removing visible error messages from the UI.</li>
     * </ul>
     */
    public void cleanup() {
        rowIndex = -1;
        lblError.setText("");
    }


    /**
     * Handles the action triggered by clicking the "Handle Order" button.
     * This method processes the selected order from the table and prepares it for an alternative
     * order creation. If no order is selected, it displays a message prompting the user to select an order.
     * After selecting an order, it updates the temporary order object with visitation date and time,
     * then navigates to the "Create Alternative Order" page and passes the order and full name to that page's controller.
     *
     * @param event The {@link ActionEvent} triggered by clicking the button. This parameter is not used
     *              directly in the method but is required by the FXML button click handler signature.
     *              <p>
     *              Pre-conditions:
     *              - An order must be selected from the table (i.e., {@code rowIndex} should not be -1).
     *              - The {@code list} must contain timestamps corresponding to each order, indexed appropriately.
     *              <p>
     *              Post-conditions:
     *              - If no order is selected, an error message is displayed to the user.
     *              - If an order is selected, the {@code tempOrder} object is updated with the visitation date and time,
     *              and the application navigates to the "Create Alternative Order" page, passing along the necessary data.
     *              <p>
     *              Notes:
     *              - This method assumes the existence of an {@code applicationWindowController} that manages application windows
     *              and a {@code CreateAlternativeOrderController} for setting the fields of the alternative order creation page.
     */
    @FXML
    public void OnClickHandleOrderButton(ActionEvent event) {
        if (rowIndex == -1) {
            System.out.println("You must select an order");
            lblStatusMsg.setText("You must select an order");
            return;
        }
        Timestamp t1 = list.get(rowIndex);
        tempOrder.setVisitationDate(t1);
        tempOrder.setEnteredTime(t1);
        applicationWindowController.loadVisitorsPage("CreateAlternativeOrder");
        Object controller = applicationWindowController.getCurrentActiveController();
        if (controller instanceof CreateAlternativeOrderController) {
            ((CreateAlternativeOrderController) controller).setFields(tempOrder, tempfullName);
        }

    }


    /**
     * Initiates the process of retrieving available spots for a given order from the server and updating the user interface
     * with the retrieved information. It sends a request to the server with the order details and handles the server's response.
     * The method displays an error message using a popup if any issue occurs during the process, such as a database error
     * or an unexpected server response. If the response is successful and contains an ArrayList of timestamps, it proceeds
     * to populate a table with these timestamps.
     *
     * @param o1  The order for which available spots are being requested. This {@link Order} object contains the necessary
     *            details required by the server to process the request.
     * @param str An additional string parameter provided to the method. Its specific use is not described but could
     *            be related to further specifying the request or processing the response.
     *            <p>
     *            The method performs the following key operations:
     *            1. Constructs and sends a message to the server requesting available spots for the specified order.
     *            2. Waits for and processes the server's response, handling different scenarios such as database errors or
     *            incorrect response types through the display of relevant error messages in popups.
     *            3. On successful retrieval of the expected response (an ArrayList of Timestamps), calls another method to
     *            populate a table with this data.
     *            4. Clears any existing error messages displayed in the user interface.
     *            <p>
     *            It is crucial that the {@code ClientUI.client} and {@code applicationWindowController} are properly initialized
     *            and capable of sending requests to and displaying popups within the application, respectively.
     *            <p>
     *            Note: The use of global state (e.g., {@code ClientCommunicator.msg}) for fetching the server response
     *            indicates a reliance on shared state that could affect the method's reusability and testability.
     */
    public void start(Order o1, String str) {
        Message send = new Message(OpCodes.OP_GET_AVAILABLE_SPOTS, applicationWindowController.getUser().getUsername(), o1);
        ClientUI.client.accept(send);
        Message response = ClientCommunicator.msg;
        OpCodes returnOpCode = response.getMsgOpcode();
        if (returnOpCode == OpCodes.OP_DB_ERR) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        // Checking if the response from the server is inappropriate.
        if (returnOpCode != OpCodes.OP_GET_AVAILABLE_SPOTS) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        if (!(response.getMsgData() instanceof ArrayList)) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        populateTable((ArrayList<Timestamp>) (ClientCommunicator.msg.getMsgData()), o1, str);
        lblError.setText("");
    }


    /**
     * Populates a table with available timestamps for an order and manages the user interface based on the provided data.
     * This method takes a list of timestamps, representing available spots for an order, and updates a table view
     * to display these times. If the provided list is empty, it disables a specific button and shows a popup
     * notification to the user. It also handles navigation based on the current UI state.
     *
     * @param dataList An {@link ArrayList} of {@link Timestamp} objects, each representing an available time slot
     *                 that can be booked for the specified order.
     * @param o1       The {@link Order} object for which the available spots are intended. This object's details
     *                 might be used in further processing or displayed within the UI.
     * @param fullName The full name of the individual associated with the order, possibly used for display
     *                 or logging purposes within the method or subsequent UI updates.
     *                 <p>
     *                 Key Operations:
     *                 - Clears the existing list of timestamps and disables the 'choose' button if the provided data list is empty,
     *                 indicating no available alternative times.
     *                 - Shows a temporary message popup to inform the user when no alternative times are available.
     *                 - Sets up a delay to navigate back to a specific page within the application after displaying the no availability message.
     *                 - For non-empty data lists, updates a table view with the dates and times extracted from the provided timestamps.
     *                 - Stores the provided order and fullName for potential use in other operations triggered by UI interactions.
     *                 <p>
     *                 Note: This method assumes that the UI elements like {@code LblChooseBtn} and {@code tableOrders} are properly
     *                 initialized and that the application window controller is configured to handle page navigation and popups.
     *                 It further assumes the existence of helper methods like {@code parseVisitDate} and {@code parseVisitTime}
     *                 for processing timestamp data into a displayable format.
     */
    @FXML
    public void populateTable(ArrayList<Timestamp> dataList, Order o1, String fullName) {
        list.clear();
        if (dataList.isEmpty()) {
            LblChooseBtn.setDisable(true);
            MessagePopup popup = new MessagePopup("No another alternative time", Duration.seconds(5), 300, 150, false);
            popup.show(applicationWindowController.getRoot());
            if (applicationWindowController.isMenuSlider()) {
                new Timeline(new KeyFrame(Duration.seconds(5), ae -> applicationWindowController.setCenterPage("/VisitorsUI/VisitorOrderVisitationPage.fxml"))).play();
            } else {
                new Timeline(new KeyFrame(Duration.seconds(5), ae -> applicationWindowController.setCenterPageForNewVisitor("/VisitorsUI/VisitorOrderVisitationPage.fxml", applicationWindowController.getUser(), "/CommonClient/gui/LeftBackground.fxml"))).play();
            }
        }
        tempfullName = fullName;
        tempOrder = o1;
        rowIndex = -1;
        ObservableList<Map<String, String>> tableData = FXCollections.observableArrayList();
        for (Timestamp item : dataList) {
            list.add(item);
            String date = parseVisitDate(item);
            String time = parseVisitTime(item);
            Map<String, String> row = new HashMap<>();
            row.put("Date", date);
            row.put("Time", time.substring(0, time.length() - 3));
            tableData.add(row);
        }
        tableOrders.setItems(tableData);
    }


    /**
     * Sets up the functionality to make rows clickable in a JavaFX TableView.
     * Clicking on a row will print out the data of the clicked row.
     */
    private void makeRowClickable() {
        // Set row factory to handle clicks on rows
        tableOrders.setRowFactory(tv -> {
            TableRow<Map<String, String>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                    Map<String, String> clickedRowData = row.getItem();
                    rowIndex = row.getIndex();
                    System.out.println("Selected row data: " + clickedRowData);
                }
            });
            return row;
        });
    }


    /**
     * Initializes the controller after its root element has been completely processed.
     * Sets up the column cell value factories and makes rows clickable.
     *
     * @param location  The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resources The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colDate.setCellValueFactory(new MapValueFactory<>("Date"));
        colTime.setCellValueFactory(new MapValueFactory<>("Time"));
        makeRowClickable();
    }
}
