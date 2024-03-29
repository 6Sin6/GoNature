package CommonUtils;

import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;


/**
 * Base class for all popups in the application.
 */
public abstract class BasePopup {
    protected BorderPane root;
    protected VBox popup = new VBox(15);
    protected StackPane modalLayer = new StackPane();
    protected boolean fullScreenMode;
    private Node previousPageChild;
    protected int popupWidth;
    protected int popupHeight;

    /**
     * Constructor for BasePopup class. This constructor is used to create a popup with default width and height.
     *
     * @param fullScreen - If true, the popup will be displayed in full screen mode.
     * @param width      - Width of the popup.
     * @param height     - Height of the popup.
     */
    public BasePopup(boolean fullScreen, int width, int height) {
        this.fullScreenMode = fullScreen;
        this.popupWidth = fullScreen ? 1500 : width;
        this.popupHeight = fullScreen ? 1100 : height;
        setupPopup();
    }

    /**
     * Sets up the visual components of the popup.
     */
    private void setupPopup() {
        popup.setMaxSize(popupWidth, popupHeight);
        popup.setPrefSize(popupWidth, popupHeight);
        popup.setAlignment(Pos.CENTER);
        popup.setStyle("-fx-background-color: #263238;");
        //modalLayer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4);");
        modalLayer.setAlignment(Pos.CENTER);
        modalLayer.getChildren().add(popup);
        modalLayer.setVisible(false);
        popup.setTranslateY(-1200); // Initially off-screen
    }

    /**
     * Displays the popup within the specified parent BorderPane.
     *
     * @param parent The parent BorderPane where the popup will be shown.
     */
    public void show(BorderPane parent) {
        this.root = parent;
        if (!parent.getChildren().contains(modalLayer)) {
            previousPageChild = parent.getCenter();
            if (!fullScreenMode) {
                modalLayer.getChildren().add(parent.getCenter());
                popup.toFront();
            }
            parent.setCenter(modalLayer);
        }
        modalLayer.setVisible(true);
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.3), popup);
        transition.setFromY(-1200);
        transition.setToY(0);
        transition.play();
    }

    /**
     * Closes the popup.
     *
     * @param onCloseNavigateToPage Whether the popup is being closed due to navigation.
     */
    public void closePopup(boolean onCloseNavigateToPage) {
        TranslateTransition closeTransition = new TranslateTransition(Duration.seconds(0.3), popup);
        closeTransition.setToY(-1200);
        closeTransition.setOnFinished(event -> {
            modalLayer.setVisible(true);
            if (onCloseNavigateToPage) {
                return;
            }
            if (this.fullScreenMode) {
                this.root.setCenter(previousPageChild);
            }
        });
        closeTransition.play();
    }
}
