package EmployeesControllers;

import CommonClient.ClientUI;
import CommonClient.controllers.BaseController;
import CommonUtils.CommonUtils;
import CommonUtils.ConfirmationPopup;
import CommonUtils.InputTextPopup;
import Entities.Message;
import Entities.OpCodes;
import client.ClientCommunicator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import static Entities.OpCodes.OP_UPDATE_EXIT_TIME_OF_ORDER;

/**
 * Abstract controller class providing common functionality for general employee dashboard pages.
 * This class defines methods for handling user interactions such as submitting data and navigating
 * to different pages within the application.
 */
public abstract class GeneralEmployeeDashboardController extends BaseController {
    protected InputTextPopup popup;

    public void cleanup() {
        // No cleanup required
    }

    /**
     * Handles the submission of input data, specifically for updating the exit time of an order based on the provided order ID.
     * Validates the order ID input before sending a request to the server to update the exit time. Depending on the server's
     * response, it either displays an error message or confirms the successful update of the order's exit time.
     *
     * @param inputs An array of strings containing user inputs, where {@code inputs[0]} is expected to be the order ID.
     *               <p>
     *               This method first validates the order ID to ensure it meets the application's criteria for a valid ID. If the validation
     *               fails, it displays an error message to the user via the popup and exits early from the method.
     *               <p>
     *               If the order ID is valid, it sends a message to the server requesting the update of the exit time for the specified order.
     *               The method then processes the server's response, handling potential errors such as database errors or unexpected response
     *               codes by displaying appropriate error messages through confirmation popups.
     *               <p>
     *               On successful server response indicating the exit time has been updated, it sets the popup message color and displays
     *               a success message. If the server returns a non-empty error message, it is displayed to the user in red.
     *               <p>
     *               This method provides robust error handling and user feedback, ensuring that users are informed of the outcome of their
     *               submission and any issues encountered during the process.
     */
    protected void onSubmit(String[] inputs) {
        String orderID = inputs[0];
        if (!CommonUtils.isValidOrderID(orderID)) {
            popup.setErrorLabel("Invalid Order ID");
            return;
        }
        Object message = new Message(OP_UPDATE_EXIT_TIME_OF_ORDER, null, orderID);
        ClientUI.client.accept(message);
        Message response = ClientCommunicator.msg;
        OpCodes returnOpCode = response.getMsgOpcode();
        if (returnOpCode == OpCodes.OP_DB_ERR) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.DB_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }
        // Checking if the response from the server is inappropriate.
        if (returnOpCode != OpCodes.OP_UPDATE_EXIT_TIME_OF_ORDER) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }

        if (!(response.getMsgData() instanceof String)) {
            ConfirmationPopup confirmationPopup = new ConfirmationPopup(CommonUtils.SERVER_ERROR, applicationWindowController, 800, 400, true, "OK", true);
            confirmationPopup.show(applicationWindowController.getRoot());
            return;
        }

        String answer = ClientCommunicator.msg.getMsgData().toString();
        if (!answer.equals("")) {
            popup.setLabelColor("#FF0000");
            popup.setErrorLabel(answer);
        } else {
            popup.setLabelColor("#00FF00");
            popup.setErrorLabel("Order Exited Successfully!");
        }
    }


    /**
     * Responds to the action of clicking the "Available Spot" button by navigating the user to the
     * "Check Available Spots Page." This method is part of the application's UI flow, allowing users, typically
     * employees or park managers, to view the current availability of spots in various parks or facilities
     * managed by the application.
     * The controller invokes the start method of the CheckAvailableSpotsController to initialize the page.
     * Upon invocation, this method leverages the {@code applicationWindowController} to switch the current view
     * to the page dedicated to checking spot availability. It ensures that the employees or managers can access
     * the functionality to monitor and manage the occupancy and availability of the managed areas effectively.
     *
     * @param ignoredEvent The {@link ActionEvent} triggered by clicking the button. While not directly used in
     *                     the method, it's required by the FXML framework for event handling. This parameter allows
     *                     the method to be connected to UI components defined in FXML.
     */
    @FXML
    public void OnClickAvailableSpotButton(ActionEvent ignoredEvent) {
        applicationWindowController.loadEmployeesPage("CheckAvailableSpotsPage");
        Object controller = applicationWindowController.getCurrentActiveController();
        if (controller instanceof CheckAvailableSpotsController) {
            ((CheckAvailableSpotsController) controller).start();
        }
    }


    /**
     * Handles the action of clicking the "Generate Bill" button, initiating the transition to the
     * "Generate Bill Page." This method enables users, such as employees or financial administrators,
     * to navigate to a dedicated page for generating bills or invoices for services rendered or for
     * other financial transactions managed by the application.
     * <p>
     * The method utilizes the {@code applicationWindowController} to change the current display to the
     * Generate Bill Page, streamlining the process of bill generation and management within the application's
     * user interface. It simplifies access to financial operations, ensuring that users can efficiently
     * proceed with generating necessary financial documents.
     *
     * @param ignoredEvent The {@link ActionEvent} triggered by the user's click on the button. Although this
     *                     parameter is not utilized within the method's body, it is essential for the method's
     *                     signature to comply with the FXML framework's requirements for handling user interactions
     *                     with UI elements.
     */
    @FXML
    public void OnClickGenerateBillButton(ActionEvent ignoredEvent) {
        applicationWindowController.loadEmployeesPage("GenerateBillPage");
    }

    /**
     * Event handler method for when the "Exit" button is clicked.
     * This method creates a popup window prompting the user to enter an order ID.
     * The popup window is displayed relative to the root node of the application's UI.
     *
     * @param ignoredEvent The ActionEvent triggered by clicking the "Exit" button. This parameter is currently ignored.
     */
    @FXML
    void OnClickExitButton(ActionEvent ignoredEvent) {
        popup = new InputTextPopup(new String[]{"Enter Order ID"}, this::onSubmit, 0, 0, true, true, false);
        popup.show(applicationWindowController.getRoot());
    }
}
