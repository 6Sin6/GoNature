package CommonUtils;

import CommonClient.ClientUI;
import CommonClient.controllers.ApplicationWindowController;
import Entities.Message;
import Entities.OpCodes;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * ConfirmationPopup represents a custom popup for displaying confirmation messages.
 * This popup extends the BasePopup class.
 */
public class ConfirmationPopup extends BasePopup {
    private Button yesButton = new Button();
    private Button noButton = new Button();
    private final String DB_ERROR_MSG = "An error occurred while trying to connect to the database. Please try again later.";
    private final String SERVER_ERROR_MSG = "An error occurred while trying to connect to the server. Please try again later.";

    /**
     * Constructs a ConfirmationPopup with custom buttons and behavior.
     *
     * @param question       The confirmation question to display.
     * @param onConfirm      The action to execute when the user confirms.
     * @param onCancel       The action to execute when the user cancels.
     * @param width          The width of the popup.
     * @param height         The height of the popup.
     * @param fullScreenMode Whether the popup should be in full-screen mode.
     * @param FirstBtn       Label for the first button.
     * @param SecondBtn      Label for the second button.
     * @param exitOnOut      Whether clicking outside the popup should close it.
     */
    public ConfirmationPopup(String question, Runnable onConfirm, Runnable onCancel, int width, int height, boolean fullScreenMode, String FirstBtn, String SecondBtn, boolean exitOnOut) {
        super(fullScreenMode, width, height);
        yesButton.setText(FirstBtn);
        noButton.setText(SecondBtn);
        this.yesButton.getStyleClass().add("menu-item-big");
        this.noButton.getStyleClass().add("menu-item-big");
        Label questionLabel = new Label(question);
        questionLabel.setStyle("-fx-font-size: 20px;-fx-text-fill: white;");
        popup.getChildren().addAll(questionLabel, yesButton, noButton);

        yesButton.setOnAction(e -> {
            onConfirm.run();
            closePopup(false);
        });

        noButton.setOnAction(e -> {
            if (onCancel != null) onCancel.run();
            closePopup(false);
        });
        if (exitOnOut) {
            modalLayer.setOnMouseClicked(e -> {
                if (!popup.getBoundsInParent().contains(e.getSceneX(), e.getSceneY())) {
                    closePopup(false);
                }
            });
        }
    }

    /**
     * Constructs a ConfirmationPopup with a single confirmation button.
     *
     * @param question       The confirmation question to display.
     * @param onConfirm      The action to execute when the user confirms.
     * @param width          The width of the popup.
     * @param height         The height of the popup.
     * @param fullScreenMode Whether the popup should be in full-screen mode.
     * @param FirstBtn       Label for the confirmation button.
     * @param exitOnOut      Whether clicking outside the popup should close it.
     */
    public ConfirmationPopup(String question, Runnable onConfirm, int width, int height, boolean fullScreenMode, String FirstBtn, boolean exitOnOut) {
        super(fullScreenMode, width, height);
        yesButton.setText(FirstBtn);
        yesButton.getStyleClass().add("menu-item-big");
        Label questionLabel = new Label(question);
        questionLabel.setStyle("-fx-font-size: 20px;-fx-text-fill: white;");
        popup.getChildren().addAll(questionLabel, yesButton);

        yesButton.setOnAction(e -> {
            onConfirm.run();
            closePopup(false);
        });
        if (exitOnOut) {
            modalLayer.setOnMouseClicked(e -> {
                if (!popup.getBoundsInParent().contains(e.getSceneX(), e.getSceneY())) {
                    closePopup(false);
                }
            });
        }
    }

    /**
     * Constructs a ConfirmationPopup with customizable parameters.
     *
     * @param ErrorMsg       An integer representing the type of error (1 for database error, 2 for server error).
     * @param appController  The ApplicationWindowController instance.
     * @param width          Width of the popup.
     * @param height         Height of the popup.
     * @param fullScreenMode Whether the popup should be in full-screen mode.
     * @param FirstBtn       Label for the "yes" button.
     * @param exitOnOut      Whether the popup should close when clicked outside.
     */
    public ConfirmationPopup(Integer ErrorMsg, ApplicationWindowController appController, int width, int height, boolean fullScreenMode, String FirstBtn, boolean exitOnOut) {
        super(fullScreenMode, width, height);
        String question = "";
        switch (ErrorMsg) {
            case 1:
                question = DB_ERROR_MSG;
                break;
            case 2:
                question = SERVER_ERROR_MSG;
                break;
        }
        yesButton.setText(FirstBtn);
        yesButton.getStyleClass().add("menu-item-big");
        Label questionLabel = new Label(question);
        questionLabel.setStyle("-fx-font-size: 20px;-fx-text-fill: white;");
        popup.getChildren().addAll(questionLabel, yesButton);

        yesButton.setOnAction(e -> {
            if (appController.getUser() != null) {
                Object msg = new Message(OpCodes.OP_LOGOUT, appController.getUser().getUsername(), null);
                ClientUI.client.accept(msg);
            }
            String pathTorRoute = "/CommonClient/gui/HomePage.fxml";
            appController.setCenterPage(pathTorRoute);
        });
        if (exitOnOut) {
            modalLayer.setOnMouseClicked(e -> {
                if (!popup.getBoundsInParent().contains(e.getSceneX(), e.getSceneY())) {
                    closePopup(false);
                }
            });
        }
    }
}
