// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 
package GoNatureServer;

import CommonServer.ocsf.AbstractServer;
import CommonServer.ocsf.ConnectionToClient;
import DataBase.DBConnection;
import Entities.Message;
import Entities.OpCodes;
import Entities.Role;
import Entities.User;
import ServerUIPageController.ServerPortFrameController;

import java.io.IOException;
import java.sql.SQLException;


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

    private boolean connected = true;


    //Constructors ****************************************************

    /**
     * Constructs an instance of the echo server.
     *
     * @param port The port number to connect on.
     */

    private GoNatureServer(int port, ServerPortFrameController controller) throws Exception {
        super(port);
        this.controller = controller;
    }

    public void initializeDBConnection(ServerPortFrameController controller) throws Exception {
        try {
            db = DBConnection.getInstance(controller);
        } catch (ClassNotFoundException | SQLException e) {
            controller.addtolog(e.getMessage());
            db = null;
            controller.addtolog("Server failed to initialize");
            connected = false;
        }
    }

    public static GoNatureServer getInstance(int port, ServerPortFrameController controller) throws Exception {
        if (server == null) {
            server = new GoNatureServer(port, controller);
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
        if (!connected) {
            try {
                closeServer();
                return;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        this.controller.addtolog("Server listening for connections on port " + getPort());
        server.controller.toggleControllers(true);
    }

    public void handleMessageFromClient(Object msg, ConnectionToClient client) throws IOException {
        Message newMsg = new Message(null, null, null);

        if (msg instanceof String) {
            if (msg.equals("quit")) {
                this.controller.addtolog("Client " + client + " Disconnected");
                controller.removeRowByIP(client.getInetAddress().getHostAddress());
                client.close();
                return;
            }
        }

        if (msg instanceof Message) {
            switch (((Message) msg).getMsgOpcode()) {
                case OP_SYNC_HANDSHAKE:
                    client.sendToClient(msg);
                case OP_SIGN_IN:
                    User userCredentials = (User) ((Message) msg).getMsgData();
                    User authenticatedUser = db.login(userCredentials.getUsername(), userCredentials.getPassword());
                    if (authenticatedUser == null) {
                        Message respondMsg = new Message(OpCodes.OP_SIGN_IN, "", null);
                        client.sendToClient(respondMsg);
                        return;
                    }
                    Message respondMsg = new Message(OpCodes.OP_SIGN_IN, authenticatedUser.getUsername(), authenticatedUser);
                    client.sendToClient(respondMsg);
                default:
                    controller.addtolog("Error Unknown Opcode");
            }
        }
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
        if (db != null) {
            db.closeConnection();
        }
    }

    public static void closeServer() {
        try {
            if (server == null) {
                System.out.println("Server isn't initialized");
                return;
            }
            server.sendToAllClients("Disconnect");
            server.stopListening();
            server.close();
            server.controller.toggleControllers(false);
            server = null;
        } catch (Exception e) {
            server.controller.addtolog("Server isn't initialized");
        }
    }

    @Override
    protected void clientConnected(ConnectionToClient client) {
        controller.addRow(client.getInetAddress().getHostName(), client.getInetAddress().getHostAddress());
    }


}
//End of EchoServer class
