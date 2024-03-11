package CommonUtils;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class MessagePopup extends BasePopup {
    public MessagePopup(String message, Duration duration) {
        super();
        Label messageLabel = new Label(message);
        popup.getChildren().add(messageLabel);

        // Close the popup after the specified duration
        new Timeline(new KeyFrame(duration, ae -> closePopup())).play();
    }
}
