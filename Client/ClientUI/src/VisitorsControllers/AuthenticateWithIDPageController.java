package VisitorsControllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AuthenticateWithIDPageController {


    @FXML
    private StackPane StackPane;

    @FXML
    private Pane Pane;

    @FXML
    private ImageView im1;

    @FXML
    private Separator sep;

    @FXML
    private VBox menu;

    @FXML
    private Text header;

    @FXML
    private Text text2;

    @FXML
    private Text text1;

    @FXML
    private MFXButton SubmitBtn;

    @FXML
    private MFXTextField IDTextField;

    @FXML
    private Text ErrorText;

    private String getIDFromTxtBox()
    {
        return IDTextField.getText();
    }
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/VisitorsUI/AuthenticateWithIDPage.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("GoNature - Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

}
