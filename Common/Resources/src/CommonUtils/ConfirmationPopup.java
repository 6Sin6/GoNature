package CommonUtils;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ConfirmationPopup extends BasePopup {
    private Button yesButton = new Button("Yes");
    private Button noButton = new Button("No");

    public ConfirmationPopup(String question, Runnable onConfirm, Runnable onCancel) {
        super();
        Label questionLabel = new Label(question);
        popup.getChildren().addAll(questionLabel, yesButton, noButton);

        yesButton.setOnAction(e -> {
            onConfirm.run();
            closePopup();
        });

        noButton.setOnAction(e -> {
            if (onCancel != null) onCancel.run();
            closePopup();
        });

        modalLayer.setOnMouseClicked(e -> {
            if (!popup.getBoundsInParent().contains(e.getSceneX(), e.getSceneY())) {
                closePopup();
            }
        });
    }
}
