package VisitorsUI;
import Server.GoNatureServer;
import VisitorsControllers.DashboardPageContoller;
import VisitorsControllers.OrderDetailsPageController;
import client.ClientController;
import javafx.application.Application;

import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;


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
		primaryStage.setOnCloseRequest(e -> Platform.runLater(()-> {
            client.quit();
        }));
		aFrame.start(primaryStage);
	}


}
