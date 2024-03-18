package CommonUtils;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.function.Consumer;

public class InputTextPopup extends BasePopup {
    private ArrayList<MFXTextField> textFields = new ArrayList<>();
    private MFXButton confirmButton = new MFXButton("Confirm");
    private ImageView backButton;
    private Label errorLabel = new Label();

    public void setErrorLabel(String msg) {
        errorLabel.setText(msg);
    }

    public void setLabelColor(String color) {
        errorLabel.setStyle("-fx-text-fill: " + color + ";" +
                "-fx-font-size: 20px; " +
                "-fx-padding: 10px 10px; " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 5px; " +
                "-fx-alignment: center;");
    }

    public InputTextPopup(String[] prompts,
                          Consumer<String[]> onConfirm,
                          int width,
                          int height,
                          boolean fullScreenMode,
                          boolean displayBackButton,
                          boolean onCloseNavigateToPage) {
        super(fullScreenMode, width, height);

        if (displayBackButton) {
            backButton = new ImageView(new Image("/assets/backButtonWhite.png", 50, 50, true, true));
            backButton.setOnMouseClicked(e -> closePopup(false));
            backButton.setStyle("-fx-cursor: hand;");
            modalLayer.getChildren().add(backButton);
            StackPane.setAlignment(backButton, Pos.TOP_LEFT);
            StackPane.setMargin(backButton, new Insets(15));
        }

        // For each prompt, create a label and a text field.
        for (String prompt : prompts) {
            Label promptLabel = new Label(prompt);
            promptLabel.setStyle("-fx-text-fill: #FCFCFC; -fx-font-size: 24px; -fx-padding: 10px 10px");
            popup.getChildren().add(promptLabel);

            MFXTextField textField = new MFXTextField();
            textField.setMaxWidth(320);
            popup.getChildren().add(textField);
            textFields.add(textField);
        }

        errorLabel.setStyle("-fx-text-fill: #B22222; " +
                "-fx-font-size: 20px; " +
                "-fx-padding: 10px 10px; " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 5px; " +
                "-fx-alignment: center;");

        confirmButton.getStyleClass().add("menu-item-big");
        confirmButton.setStyle("-fx-padding: 20px 0;");
        popup.getChildren().addAll(confirmButton, errorLabel);

        confirmButton.setOnAction(e -> {
            if (onConfirm != null) {
                onConfirm.accept(getTexts());
            }
            if (errorLabel.getText().isEmpty()) {
                closePopup(onCloseNavigateToPage);
            }
        });

        if (!fullScreenMode) {
            modalLayer.setOnMouseClicked(e -> {
                if (!popup.getBoundsInParent().contains(e.getSceneX(), e.getSceneY())) {
                    closePopup(onCloseNavigateToPage);
                }
            });
        }
    }

    public String[] getTexts() {
        String[] texts = new String[textFields.size()];
        for (MFXTextField textField : textFields) {
            if (textField.getText().isEmpty()) {
                errorLabel.setText("All fields are required");
                return texts;
            }
            texts[textFields.indexOf(textField)] = textField.getText();
        }
        return texts;
    }
}
