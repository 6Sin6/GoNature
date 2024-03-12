package VisitorsControllers;

import CommonClient.controllers.BaseController;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyComboBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class VisitorOrderVisitationPageController extends BaseController {

    @FXML
    private TextField EmailLbl;

    @FXML
    private Text Header;

    @FXML
    private ImageView appLogo;

    @FXML
    private Label appName;

    @FXML
    private ImageView backButton;

    @FXML
    private MFXButton createOrderBtn;

    @FXML
    private Label dashboardLabel;

    @FXML
    private Label dateLbl;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Label emailLbl;

    @FXML
    private TextField fNameText;

    @FXML
    private Label firstNameLbl;

    @FXML
    private Label homePageLabel;

    @FXML
    private Label lastNameLbl;

    @FXML
    private TextField lastNameText;

    @FXML
    private StackPane logoContainer;

    @FXML
    private MFXButton logoutBtnMenu;

    @FXML
    private VBox menuBox;

    @FXML
    private Label numOfVisitorsLbl;

    @FXML
    private TextField numOfVisitorsText;

    @FXML
    private Label ordersLabel;

    @FXML
    private Pane pane;

    @FXML
    private MFXLegacyComboBox<?> parkCmbBox;

    @FXML
    private Label parkLbl;

    @FXML
    private Label phoneLbl;

    @FXML
    private Separator sepOrder;

    @FXML
    private StackPane stackPane;

    @FXML
    private MFXLegacyComboBox<?> timeOfVisitCmbBox;

    @FXML
    private Label timeOfVisitLbl;

    @FXML
    private Label userRoleLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    void OnClickCreateOrderButton(ActionEvent event) {

    }

    public void start(Stage primaryStage) throws Exception {
        Parent root = (Parent) FXMLLoader.load(this.getClass().getResource("/VisitorsUI/VisitorOrderVisitationPage.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("GoNature - Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
