package Server;

import javafx.application.Application;
import javafx.stage.Stage;
import logic.Student;
import java.util.Vector;
import gui.ServerPortFrameController;


public class ServerUI extends Application {

	final public static int DEFAULT_PORT = 5555;
	public static Vector<Student> students=new Vector<Student>();

	public static void main( String args[] )
	   {   
		 launch(args);
	  } // end main
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub				  		
		ServerPortFrameController aFrame = new ServerPortFrameController();
		 
		aFrame.start(primaryStage);
	}
	
	public static void runServer(ServerPortFrameController guiController)
	{
		 EchoServer sv;
		 int port = 0; //Port to listen on

	        try
	        {
	        	port = Integer.parseInt(guiController.getPort()); //Set port to 5555
	          
	        }
	        catch(Throwable t)
	        {
	        	System.out.println("ERROR - Could not connect!");
	        }
	    	try {
				sv = new EchoServer(port, guiController);
				try
				{
					sv.listen(); //Start listening for connections
				}
				catch (Exception ex)
				{
					System.out.println("ERROR - Could not listen for clients!");
				}

			}
			catch (Exception e){
				guiController.addtolog("");
			}
	}
	

}
