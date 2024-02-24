package VisitorsUI;
import client.ClientController;
import javafx.application.Application;

import javafx.stage.Stage;

import VisitorsControllers.AcademicFrameController;

public class ClientUI extends Application {
	public static ClientController chat; //only one instance

	public static void main( String args[] ) throws Exception
	   { 
		    launch(args);  
	   } // end main
	 
	@Override
	public void start(Stage primaryStage) throws Exception {
		 chat= new ClientController("192.168.194.206", 5555);
		// TODO Auto-generated method stub
						  		
		AcademicFrameController aFrame = new AcademicFrameController(); // create StudentFrame
		 
		aFrame.start(primaryStage);
	}
	
	
}
