// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 
package Server;

import java.io.*;
import java.util.Vector;

import logic.Faculty;
import logic.Student;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */

public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  //final public static int DEFAULT_PORT = 5555;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   * 
   */
 public static Student [] students=new Student[4];

  public EchoServer(int port) 
  {
    super(port);
  }

  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   * @param 
   */
  public void handleMessageFromClient  (Object msg, ConnectionToClient client)
  {
        int flag = 0;
        System.out.println("Message received: " + msg + " from " + client);
        if (msg.toString().startsWith("save: ")) {
            for (int i = 0; i < students.length; i++) {
                  if (students[i].getId().equals(this.extractId(msg.toString()))) {
                        students[i].setFc(this.extractFaculty(msg.toString()));
                        students[i].setLName(this.extractLName(msg.toString()));
                        students[i].setPName(this.extractPName(msg.toString()));
                        this.sendToAllClients("updated");
                        flag = 1;
                        break;
                  }
            }
        } else {
            for (int i = 0; i < students.length; i++) {
                if (students[i].getId().equals(msg)) {
                    System.out.println("Student Found");
                    this.sendToAllClients(students[i].toString());
                    flag = 1;
                    break;
                }
            }
        }

        if (flag != 1) {
            System.out.println("Student Not Found");
            this.sendToAllClients("Error");
        }
  }

  private String extractId(String msg) {
      String[] result = msg.split("\\s");
      return result[1];
  }

  private String extractLName(String msg) {
      String[] result = msg.split("\\s");
      return result[3];
  }

  private String extractPName(String msg) {
      String[] result = msg.split("\\s");
      return result[2];
  }

  private Faculty extractFaculty(String msg) {
      String[] result = msg.split("\\s");
      return new Faculty(result[4], result[5]);
  }
   
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println ("Server listening for connections on port " + getPort());
    students[0]=new Student("12345","Yossi","Cohen",new Faculty("SE","9901000"));
    students[1]=new Student("66666","Yossefa","Levi",new Faculty("IE","9901123"));
    students[2]=new Student("77777","moshe","galili",Faculty.getFaculty("SE"));
    students[3]=new Student("77778","moran","galil",Faculty.getFaculty("SE")); 

  }
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()  {
    System.out.println ("Server has stopped listening for connections.");
  }  
}
//End of EchoServer class
