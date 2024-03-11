package CommonUtils;

import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public abstract class BasePopup {
    protected StackPane root = new StackPane();
    protected VBox popup = new VBox(10);
    protected StackPane modalLayer = new StackPane();

    public BasePopup() {
        setupPopup();
    }

    private void setupPopup() {
        popup.setMaxSize(200,120);
        popup.setAlignment(Pos.CENTER);
        popup.setStyle("-fx-background-color: lightblue; -fx-border-color: black; -fx-border-width: 2;");
        //modalLayer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4);");
        modalLayer.setAlignment(Pos.CENTER);
        modalLayer.getChildren().add(popup);
        modalLayer.setVisible(false);
        popup.setTranslateY(-1200); // Initially off-screen
    }

    public void show(StackPane parent) {
        if (!parent.getChildren().contains(modalLayer)) {
            parent.getChildren().add(modalLayer);
        }
        modalLayer.setVisible(true);
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), popup);
        transition.setFromY(-1200);
        transition.setToY(0);
        transition.play();
    }

    protected void closePopup() {
        TranslateTransition closeTransition = new TranslateTransition(Duration.seconds(0.5), popup);
        closeTransition.setToY(-600);
        closeTransition.setOnFinished(event -> modalLayer.setVisible(false));
        closeTransition.play();
    }
}
