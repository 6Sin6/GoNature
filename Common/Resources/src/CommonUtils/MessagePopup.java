package CommonUtils;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class MessagePopup extends BasePopup {
    public MessagePopup(String message, Duration duration, int width, int height, boolean fullScreenMode, boolean onCloseNavigate) {
        super(fullScreenMode, width, height);
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 20px;-fx-text-fill: white;");
        popup.getChildren().add(messageLabel);

        // Close the popup after the specified duration
        new Timeline(new KeyFrame(duration, ae -> closePopup(false))).play();
    }
}
