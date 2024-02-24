package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import client.ClientUI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import Entities.Student;

public class StudentFormController implements Initializable {
	private Student s;
	@FXML
	private Label idLbl;
	@FXML
	private Button returnBtn;

	@FXML
	private Text idtext;

	@FXML
	private Label lblName;
	@FXML
	private Label lblSurname;
	@FXML
	private Label lblFaculty;
	@FXML
	private Button btnSave;
	@FXML
	private TextField txtName;
	@FXML
	private TextField txtSurname;
	
	@FXML
	private Button btnclose=null;
	
	@FXML
	private ComboBox cmbFaculty;	
	
	ObservableList<String> list;
		
	public void loadStudent(Student s1) {
		this.s=s1;
		this.idtext.setText((s.getId()));
		this.txtName.setText(s.getPName());
		this.txtSurname.setText(s.getLName());		
		this.cmbFaculty.setValue(s.getFc().getFName());
	}
	
	// creating list of Faculties
	private void setFacultyComboBox() {
		ArrayList<String> al = new ArrayList<String>();	
		al.add("ME");
		al.add("IE");
		al.add("SE");

		list = FXCollections.observableArrayList(al);
		cmbFaculty.setItems(list);
	}
	@FXML
	void SaveChange(ActionEvent event) {
		ArrayList<String> Datasend= new ArrayList<>();
		Datasend.add(this.idtext.getText());
		Datasend.add(txtName.getText());
		Datasend.add(txtSurname.getText());
		Datasend.add((String) cmbFaculty.getValue());


		ClientUI.chat.accept(Datasend);
	}
	@FXML
	void returnMain(ActionEvent event) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		((Node)event.getSource()).getScene().getWindow().hide(); //hiding primary window
		Stage primaryStage = new Stage();
		Pane root = loader.load(getClass().getResource("/gui/AcademicFrame.fxml").openStream());
		AcademicFrameController academicFrameController = loader.getController();

		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/AcademicFrame.css").toExternalForm());
		primaryStage.setTitle("AcademicFrame");

		primaryStage.setScene(scene);
		primaryStage.show();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {	
		setFacultyComboBox();		
	}
	
}
