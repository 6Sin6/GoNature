package VisitorsControllers;


import client.ChatClient;
import VisitorsUI.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public  class AcademicFrameController   {
	private StudentFormController sfc;	
	private static int itemIndex = 3;


	@FXML
	private Button btnExit = null;
	
	@FXML
	private Button btnSend = null;
	
	@FXML
	private TextField idtxt;
	
	private String getID() {
		return idtxt.getText();
	}
	
	public void Send(ActionEvent event) throws Exception {
		String id;
		FXMLLoader loader = new FXMLLoader();
		
		id=getID();
		if(id.trim().isEmpty())
		{

			System.out.println("You must enter an id number");	
		}
		else
		{
			ClientUI.chat.accept(id);
			
		
			if(ChatClient.s1.getId().equals("Error"))
			{
				System.out.println("Student ID Not Found");
			}
			else {
				System.out.println("Student ID Found");
				((Node)event.getSource()).getScene().getWindow().hide(); //hiding primary window
				Stage primaryStage = new Stage();
				Pane root = loader.load(getClass().getResource("/VisitorsControllers/StudentForm.fxml").openStream());
				StudentFormController studentFormController = loader.getController();		
				studentFormController.loadStudent(ChatClient.s1);
			
				Scene scene = new Scene(root);			
				scene.getStylesheets().add(getClass().getResource("/VisitorsControllers/StudentForm.css").toExternalForm());
				primaryStage.setTitle("Student Managment Tool");
	
				primaryStage.setScene(scene);		
				primaryStage.show();
			}
		}
	}

	public void start(Stage primaryStage) throws Exception {	
		Parent root = FXMLLoader.load(getClass().getResource("/VisitorsControllers/AcademicFrame.fxml"));
				
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/VisitorsControllers/AcademicFrame.css").toExternalForm());
		primaryStage.setTitle("Academic Managment Tool");
		primaryStage.setScene(scene);
		
		primaryStage.show();	 	   
	}
	
	public void getExitBtn(ActionEvent event) throws Exception {
		System.out.println("exit Academic Tool");
		System.exit(0);
	}
	
	public void loadStudent(Student s1) {
		this.sfc.loadStudent(s1);
	}	
	
	public  void display(String message) {
		System.out.println("message");
		
	}
	
}
