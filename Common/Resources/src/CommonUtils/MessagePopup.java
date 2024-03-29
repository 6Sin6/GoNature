package CommonUtils;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 * MessagePopup represents a custom popup for displaying messages.
 * This popup extends the BasePopup class.
 */
public class MessagePopup extends BasePopup {
    private Object controller;

    /**
     * Returns the controller associated with this MessagePopup.
     *
     * @return The controller object.
     */
    public Object getController() {
        return controller;
    }

    /**
     * Constructs a MessagePopup with a custom message and duration.
     *
     * @param message        The message to display.
     * @param duration       The duration to display the message.
     * @param width          The width of the popup.
     * @param height         The height of the popup.
     * @param fullScreenMode Whether the popup should be in full-screen mode.
     */
    public MessagePopup(String message, Duration duration, int width, int height, boolean fullScreenMode) {
        super(fullScreenMode, width, height);
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 20px;-fx-text-fill: white;");
        popup.getChildren().add(messageLabel);

        // Close the popup after the specified duration
        new Timeline(new KeyFrame(duration, ae -> closePopup(false))).play();
    }

    /**
     * Constructs a MessagePopup with a custom message and duration.
     *
     * @param pathToFXML      The page to be routed to by the poopup.
     * @param width           The width of the popup.
     * @param height          The height of the popup.
     * @param fullScreenMode  Whether the popup should be in full-screen mode.
     * @param onCloseNavigate Whether to navigate to the previous page when closing the popup.
     */
    public MessagePopup(String pathToFXML, int width, int height, boolean fullScreenMode, boolean onCloseNavigate) {
        super(fullScreenMode, width, height);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(pathToFXML));
            Parent page = loader.load();
            controller = loader.getController();
            popup.getChildren().add(page);
        } catch (Exception e) {
            e.printStackTrace();
        }

        modalLayer.setOnMouseClicked(e -> {
            if (!popup.getBoundsInParent().contains(e.getSceneX(), e.getSceneY())) {
                closePopup(onCloseNavigate);
            }
        });
    }
}
