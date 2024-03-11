package VisitorsControllers;

import CommonClient.controllers.ApplicationWindowController;
import javafx.fxml.FXML;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class VisitorDashboardPageController {
    @FXML
    private Pane Pane;

    @FXML
    private StackPane StackPane;

    @FXML
    private Text header;

    @FXML
    private ImageView im1;

    @FXML
    private VBox menu;

    @FXML
    private Separator sep;

    @FXML
    private Text text1;

    @FXML
    private Text text11;

    @FXML
    private Text text12;

    @FXML
    private Text text121;

    @FXML
    private Text text1211;

    @FXML
    private Text text1212;

    @FXML
    private Text text2;

    private ApplicationWindowController applicationWindowController;

    public void setApplicationWindowController(ApplicationWindowController applicationWindowController) {
        this.applicationWindowController = applicationWindowController;
    }

}
