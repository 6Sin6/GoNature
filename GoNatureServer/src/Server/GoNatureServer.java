// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 
package Server;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

import DataBase.DBConnection;
import ServerUIPageController.ServerPortFrameController;
import server.AbstractServer;
import server.ConnectionToClient;


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

public class GoNatureServer extends AbstractServer {
    //Class variables *************************************************

    private static GoNatureServer server;
    private ServerPortFrameController controller;
    private DBConnection db;



    //Constructors ****************************************************

    /**
     * Constructs an instance of the echo server.
     *
     * @param port The port number to connect on.
     */

    private GoNatureServer(int port, ServerPortFrameController controller) throws Exception {
        super(port);
        this.controller=controller;
        try{
            db = DBConnection.getInstance(controller);
        }
        catch (ClassNotFoundException | SQLException e){
            controller.addtolog(e.getMessage());
            super.close();
        }
    }

    public static GoNatureServer getInstance(int port, ServerPortFrameController controller) throws Exception {
        if (server == null) {
            server = new GoNatureServer(port,controller);
        }
        return server;
    }

    //Instance methods ************************************************

    /**
     * This method handles any messages received from the client.
     *
     * @param msg The message received from the client.
     * @param client The connection from which the message originated.
     * @param
     */

    /**
     * This method overrides the one in the superclass.  Called
     * when the server starts listening for connections.
     */
    protected void serverStarted() {
        this.controller.addtolog("Server listening for connections on port " + getPort());
    }



    public void handleMessageFromClient
            (Object msg, ConnectionToClient client) throws SQLException {
        if (msg instanceof String) {
            this.controller.addtolog("Message received from " + client);

        }
        else if (msg instanceof ArrayList<?>) {
            this.controller.addtolog("Message received from " + client);
            StringBuilder msgToString= new StringBuilder();
            for (String s : (ArrayList<String>) msg) {
                msgToString.append(s).append(" ");
            }
            msgToString.deleteCharAt(msgToString.length() -1);
            this.controller.addtolog(msgToString.toString());
//            try {
//                //Method from DBConnection
//                this.sendToAllClients("Update the client ");
//            } catch (SQLException e) {
//                // Prepare and send a message back to the client about the duplicate entry
//                String errorMessage = "Could not Update User Details.";
//                this.sendToAllClients(errorMessage);
//            }
        } else this.controller.addtolog("Message Error!");
    }

    /**
     * This method overrides the one in the superclass.  Called
     * when the server stops listening for connections.
     */
    @Override
    protected void serverStopped() {
        this.controller.addtolog("Server has stopped listening for connections.");
    }

    @Override
    protected void serverClosed() {
        controller.toggleControllers(false);
        this.controller.addtolog("Server has closed.");
        db.closeConnection();
    }
    public static void closeServer() throws IOException {
        if (server == null) {
            System.out.println("Server isn't initialized");
            return;
        }
        server.sendToAllClients("Disconnect");
        server.stopListening();
        server.close();
        server.controller.toggleControllers(false);
        server = null;
        System.exit(0);
    }



}
//End of EchoServer class
