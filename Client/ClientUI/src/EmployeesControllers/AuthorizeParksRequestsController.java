package EmployeesControllers;

import CommonClient.ClientUI;
import CommonClient.controllers.BaseController;
import CommonUtils.CommonUtils;
import CommonUtils.ConfirmationPopup;
import Entities.*;
import client.ClientCommunicator;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Text;

import java.sql.Timestamp;
import java.util.ArrayList;

import static CommonUtils.CommonUtils.convertMinutesToTimestamp;
import static CommonUtils.CommonUtils.parseVisitTime;

public class AuthorizeParksRequestsController extends BaseController {
    /**
     * A button used for authorizing selected requests from the table.
     */
    @FXML
    private MFXButton authBtn;

    /**
     * A table column dedicated to displaying the current status of each request.
     */
    @FXML
    private TableColumn<RequestChangingParkParameters, String> statusVal;

    /**
     * A text element used for displaying error messages related to request handling.
     */
    @FXML
    private Text errorTxt;

    /**
     * A table column for displaying the new value requested for a park parameter.
     */
    @FXML
    private TableColumn<RequestChangingParkParameters, String> newValCol;

    /**
     * A table column for displaying the name of the parameter that is requested to be changed.
     */
    @FXML
    private TableColumn<RequestChangingParkParameters, String> paramCol;

    /**
     * A table column showing the name of the park manager who made the request.
     */
    @FXML
    private TableColumn<RequestChangingParkParameters, String> parkMgrCol;

    /**
     * A table column displaying the name of the park associated with each request.
     */
    @FXML
    private TableColumn<RequestChangingParkParameters, String> parkNameCol;

    /**
     * A TableView for displaying requests to change park parameters. Each row represents a single request
     * with details such as the requested parameter, its new value, the status of the request, and identifying
     * information about the requester and the park.
     */
    @FXML
    private TableView<RequestChangingParkParameters> tableRequests;

    /**
     * A button for unauthorizing or rejecting selected requests from the table.
     */
    @FXML
    private MFXButton unAuthBtn;

    /**
     * A list of {@link RequestChangingParkParameters} objects, each representing a request to change a park parameter.
     * This list is used to populate the {@code tableRequests}.
     */
    private ArrayList<RequestChangingParkParameters> requests;

    /**
     * The index of the currently selected row in {@code tableRequests}. A value of -1 indicates that no row is selected.
     */
    private int rowIndex = -1;

    /**
     * Resets the UI components and clears the data associated with the requests table. This method is intended
     * to be called when needing to refresh the view, such as after a request has been processed or when navigating
     * away from the current view.
     * <p>
     * The method performs the following actions:
     * 1. Clears any text from the {@code errorTxt} to remove previous error messages.
     * 2. Resets the {@code rowIndex} to -1 to indicate that no table row is currently selected.
     * 3. Reinitializes the {@code requests} list to an empty list, clearing any previously loaded request data.
     * 4. Clears all items from the {@code tableRequests}, ensuring the table is ready for new data to be loaded.
     */
    public void cleanup() {
        errorTxt.setText("");
        rowIndex = -1;
        requests = new ArrayList<>();
        tableRequests.getItems().clear();
    }


    /**
     * Handles the authorization or declination of a selected park parameter change request based on user interaction.
     * The method determines the action to take (authorize or decline) based on which button was clicked, validates
     * the request if necessary (e.g., checks against park capacity when increasing visitor gap), and sends the request
     * to the server for processing.
     * <p>
     * Precondition:
     * - A request must be selected from the table ({@code rowIndex} must not be -1).
     * <p>
     * Postcondition:
     * - If a request is successfully processed (either authorized or declined), it is removed from the local list
     * and the table is updated.
     * - If an error occurs (e.g., database error, server error, validation failure), an appropriate error message
     * is displayed to the user.
     *
     * @param event The {@link ActionEvent} triggered by clicking either the authorize or decline button.
     *              <p>
     *              Validation:
     *              - If trying to authorize a request that exceeds the park's capacity, an error is displayed and the request
     *              is not sent to the server.
     *              <p>
     *              Server Communication:
     *              - Sends an authorization or declination message to the server and handles the server's response, including
     *              error handling.
     */
    @FXML
    void handleRequest(ActionEvent event) {
        if (rowIndex == -1) {
            errorTxt.setText("Please select a request to authorize.");
            return;
        }

        OpCodes opCode = event.getSource() == authBtn ? OpCodes.OP_AUTHORIZE_PARK_REQUEST : OpCodes.OP_DECLINE_PARK_REQUEST;

        RequestChangingParkParameters request = requests.get(rowIndex);

        if (opCode == OpCodes.OP_AUTHORIZE_PARK_REQUEST &&
                request.getParameter() == ParkParameters.PARK_GAP_VISITORS_CAPACITY &&
                request.getRequestedValue() > request.getPark().getCapacity()) {
            errorTxt.setText("The new requested value is higher than the park's capacity. The request cannot be authorized.");
            return;
        }

        request.setStatus(RequestStatus.REQUEST_ACCEPTED);
        Object msg = new Message(opCode, applicationWindowController.getUser().getUsername(), request);
        ClientUI.client.accept(msg);

        Message response = ClientCommunicator.msg;
        OpCodes returnOpCode = response.getMsgOpcode();
        if (returnOpCode == OpCodes.OP_DB_ERR) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        // Checking if the response from the server is inappropriate.
        if (returnOpCode != opCode) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        if (!(response.getMsgData() instanceof Boolean)) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }


        if (event.getSource() != authBtn) {
            requests.get(rowIndex).setStatus(RequestStatus.REQUEST_DECLINED);
        }
        requests.remove(rowIndex);
        setupTable();
    }


    /**
     * Configures and populates the table used to display requests for changing park parameters. This method sets up
     * the column value factories for displaying requester names, park names, parameters, their requested new values,
     * and the status of each request. It also configures a row factory to handle row selection, updating the
     * {@code rowIndex} to reflect the currently selected request.
     * <p>
     * Special formatting is applied to the display of new values for certain parameters, such as converting minutes
     * to a timestamp format for the park's default max visitation longevity.
     * <p>
     * This method clears any existing items in the table before repopulating it with the updated list of requests,
     * ensuring that the displayed data is current. The requests are provided by the {@code requests} list, which
     * should be updated with the latest data prior to calling this method.
     * <p>
     * Usage of {@code ObservableList} ensures that any changes to the {@code requests} list are reflected in the
     * TableView display, maintaining UI consistency.
     */
    private void setupTable() {
        tableRequests.getItems().clear();

        parkMgrCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRequesterName()));
        parkNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPark().getParkName()));
        paramCol.setCellValueFactory(cellData -> new SimpleStringProperty(ParkParameters.parameterToString(cellData.getValue().getParameter())));
        statusVal.setCellValueFactory(cellData -> new SimpleStringProperty(RequestStatus.statusToString(cellData.getValue().getStatus())));
        newValCol.setCellValueFactory(cellData -> {
            String formattedValue = ((Integer) cellData.getValue().getRequestedValue().intValue()).toString();
            if (cellData.getValue().getParameter() == ParkParameters.PARK_DEFAULT_MAX_VISITATION_LONGEVITY) {
                Timestamp time = convertMinutesToTimestamp(cellData.getValue().getRequestedValue().intValue());
                formattedValue = parseVisitTime(time);
            }

            return new SimpleStringProperty(formattedValue);
        });

        // Set row factory to handle clicks on rows
        tableRequests.setRowFactory(tv -> {
            TableRow<RequestChangingParkParameters> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                    RequestChangingParkParameters clickedRowData = row.getItem();
                    rowIndex = row.getIndex();
                }
            });
            return row;
        });

        ObservableList<RequestChangingParkParameters> items = FXCollections.observableArrayList(requests);
        tableRequests.getItems().addAll(items);
    }

    /**
     * Retrieves a list of park parameter change requests associated with the current park department manager from the server.
     * This method sends a request to the server to fetch all pending change requests made under the department managed
     * by the currently logged-in park department manager. Upon receiving a response, it updates the local {@code requests}
     * list with the fetched data, readying it for display in the UI.
     * <p>
     * Server responses are handled as follows:
     * - If a database error occurs, a confirmation popup displaying a database error message is shown.
     * - If the response opcode does not match the expected {@link OpCodes#OP_GET_REQUESTS_FROM_PARK_MANAGER},
     * indicating an inappropriate server response, a confirmation popup displaying a server error message is shown.
     * - If the response data is not an instance of {@code ArrayList}, indicating an unexpected data format,
     * a confirmation popup displaying a server error message is shown.
     * <p>
     * This method should be called to refresh the list of requests, such as after initially logging in as a park department
     * manager, or after processing any requests to ensure the displayed data is current.
     *
     * @see RequestChangingParkParameters for the structure of a request object.
     */
    private void getRequests() {
        ParkDepartmentManager parkDepartmentManager = (ParkDepartmentManager) applicationWindowController.getUser();
        Object msg = new Message(OpCodes.OP_GET_REQUESTS_FROM_PARK_MANAGER, parkDepartmentManager.getUsername(), parkDepartmentManager.getDepartmentID());

        ClientUI.client.accept(msg);
        Message response = ClientCommunicator.msg;

        OpCodes returnOpCode = response.getMsgOpcode();
        if (returnOpCode == OpCodes.OP_DB_ERR) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        // Checking if the response from the server is inappropriate.
        if (returnOpCode != OpCodes.OP_GET_REQUESTS_FROM_PARK_MANAGER) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        if (!(response.getMsgData() instanceof ArrayList)) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }

        requests = (ArrayList<RequestChangingParkParameters>) response.getMsgData();
    }

    /**
     * Initializes the view by fetching the latest list of park parameter change requests associated with
     * the current user (assuming the user is a Park Department Manager) and setting up the table to display these requests.
     * This method serves as an entry point for the view's initialization process, ensuring that the data displayed
     * is up-to-date and correctly formatted.
     * <p>
     * The process involves two main steps:
     * 1. Calling {@code getRequests()} to fetch the latest requests from the server. This ensures that the view
     * starts with the most current data, reflecting any changes or new requests that may have occurred.
     * 2. Calling {@code setupTable()} to configure the table's columns and populate it with the fetched requests.
     * This step ensures that the table displays the requests in a user-friendly manner, allowing the park
     * department manager to easily review and take action on each request.
     * <p>
     * This method should be invoked when the view is being prepared for display, such as after navigating to the
     * view or when the user wishes to refresh the displayed data.
     */
    public void start() {
        getRequests();
        setupTable();
    }
}
