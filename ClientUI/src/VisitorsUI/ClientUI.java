package VisitorsUI;
import VisitorsControllers.DashboardPageContoller;
import client.ClientController;
import javafx.application.Application;

import javafx.stage.Stage;


public class ClientUI extends Application {
	public static ClientController client; //only one instance

	public static void main( String args[] ) throws Exception
	{
		launch(args);
	} // end main

	@Override
	public void start(Stage primaryStage) throws Exception {
		client= new ClientController("localhost", 5555);
		// TODO Auto-generated method stub

		DashboardPageContoller aFrame = new DashboardPageContoller(); // create StudentFrame

		aFrame.start(primaryStage);
	}


}
