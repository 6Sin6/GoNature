package CommonUtils;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.scene.control.Label;

import java.util.function.Consumer;

public class InputTextPopup extends BasePopup {
    private MFXTextField textField = new MFXTextField();
    private MFXButton confirmButton = new MFXButton("Confirm");
    private Label errorLabel = new Label();

    public void setErrorLabel(String msg) {
        errorLabel.setText(msg);
    }

    public InputTextPopup(String prompt, Consumer<String> onConfirm, int width, int height, boolean fullScreenMode) {
        super(fullScreenMode, width, height);
        Label promptLabel = new Label(prompt);
        promptLabel.setStyle("-fx-text-fill: #FCFCFC; -fx-font-size: 24px; -fx-padding: 10px 10px");
        errorLabel.setStyle("-fx-text-fill: #FCFCFC; -fx-font-size: 48px; -fx-padding: 10px 10px");
        textField.setMaxWidth(320);

        confirmButton.getStyleClass().add("menu-item-big");
        popup.getChildren().addAll(promptLabel, textField, confirmButton, errorLabel);

        confirmButton.setOnAction(e -> {
            if (onConfirm != null) {
                onConfirm.accept(getText());
            }
            if (errorLabel.getText().isEmpty()) {
                closePopup();
            }
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
