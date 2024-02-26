package VisitorsUI;

import Entities.Message;
import Entities.OpCodes;
import VisitorsControllers.DashboardPageContoller;
import client.ChatClient;
import client.ClientController;
import javafx.application.Application;
import javafx.application.Platform;
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
		primaryStage.setOnCloseRequest(e -> Platform.runLater(()-> {
            client.quit();
        }));
		Message msg = new Message(OpCodes.SYNC_HANDCHECK);
		ClientUI.client.accept(msg);
		if(ChatClient.msg.GetMsgOpcode() == OpCodes.SYNC_HANDCHECK)
		{
			aFrame.start(primaryStage);
		}
		else
		{
			System.out.println("Error : Server not running");
			System.exit(0); //terminate the program after error.
		}
	}

}
