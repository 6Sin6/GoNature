package CommonUtils;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import java.util.function.Consumer;

public class InputTextPopup extends BasePopup {
    private TextField textField = new TextField();
    private Button confirmButton = new Button("Confirm");

    public InputTextPopup(String prompt, Consumer<String> onConfirm) {
        super();
        Label promptLabel = new Label(prompt);
        textField.setMaxWidth(160); // Adjust according to your layout needs
        popup.getChildren().addAll(promptLabel, textField, confirmButton);

        confirmButton.setOnAction(e -> {
            if (onConfirm != null) {
                onConfirm.accept(getText());
            }
            closePopup();
        });

        modalLayer.setOnMouseClicked(e -> {
            // Optional: Close the popup if clicking outside of it
            if (!popup.getBoundsInParent().contains(e.getSceneX(), e.getSceneY())) {
                closePopup();
            }
        });
    }

    public String getText() {
        return textField.getText();
    }
}
