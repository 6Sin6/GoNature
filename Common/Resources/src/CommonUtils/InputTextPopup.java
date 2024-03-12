package CommonUtils;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import java.util.function.Consumer;

public class InputTextPopup extends BasePopup {
    private MFXTextField textField = new MFXTextField();
    private MFXButton confirmButton = new MFXButton("Confirm");

    public InputTextPopup(String prompt, Consumer<String> onConfirm, int width, int height, boolean fullScreenMode) {
        super(fullScreenMode, width, height);
        Label promptLabel = new Label(prompt);
        promptLabel.setStyle("-fx-text-fill: #FCFCFC; -fx-font-size: 24px; -fx-padding: 10px 10px");
        textField.setMaxWidth(320);

        confirmButton.getStyleClass().add("menu-item-big");
        popup.getChildren().addAll(promptLabel, textField, confirmButton);

        confirmButton.setOnAction(e -> {
            if (onConfirm != null) {
                onConfirm.accept(getText());
            }
            closePopup();
        });

        if (!fullScreenMode) {
            modalLayer.setOnMouseClicked(e -> {
                // Optional: Close the popup if clicking outside of it
                if (!popup.getBoundsInParent().contains(e.getSceneX(), e.getSceneY())) {
                    closePopup();
                }
            });
        }
    }

    public String getText() {
        return textField.getText();
    }
}
