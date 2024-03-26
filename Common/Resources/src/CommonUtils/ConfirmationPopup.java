package CommonUtils;

import CommonClient.ClientUI;
import CommonClient.controllers.ApplicationWindowController;
import Entities.Message;
import Entities.OpCodes;
import Entities.SingleVisitor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ConfirmationPopup extends BasePopup {
    private Button yesButton = new Button();
    private Button noButton = new Button();
    private final String DB_ERROR_MSG = "An error occurred while trying to connect to the database. Please try again later.";
    private final String SERVER_ERROR_MSG = "An error occurred while trying to connect to the server. Please try again later.";

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
