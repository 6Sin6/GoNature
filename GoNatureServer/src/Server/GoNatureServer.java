// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 
package Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Enumeration;

import DataBase.DBConnection;
import Entities.Message;
import Entities.Order;
import ServerUIPageController.ServerPortFrameController;
import server.AbstractServer;
import server.ConnectionToClient;

import Entities.OpCodes;


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
        this.controller = controller;
        try {
            db = DBConnection.getInstance(controller);
        } catch (ClassNotFoundException | SQLException e) {
            controller.addtolog(e.getMessage());
            super.close();
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
        this.controller.addtolog("Server listening for connections on port " + getPort());
    }



    public void handleMessageFromClient
            (Object msg, ConnectionToClient client) throws IOException {
            Message newmsg= new Message(null,null);
            if (msg instanceof String){
                if (msg.equals("quit")) {
                    this.controller.addtolog("Client " + client+" Disconnected");
                    client.close();
                    controller.removeRowByIP(client.getInetAddress().getHostAddress());
                    return;
                }
            }
            if (msg instanceof Message){
                switch (((Message) msg).GetMsgOpcode()){
                    case GETALLORDERS:
                        if (((Message) msg).GetMsgData()==null) {
                            db.getOrders();
                            newmsg.SetMsgOpcodeValue(OpCodes.GETALLORDERS);
                            newmsg.SetMsgData(db.getOrders());
                            client.sendToClient(newmsg);
                        }
                        else{
                            controller.addtolog("Error Data Type");
                        }
                        break;
                    case GETORDERBYID:
                    if (((Message) msg).GetMsgData() instanceof Integer) {
                        newmsg.SetMsgOpcodeValue(OpCodes.GETORDERBYID);
                        newmsg.SetMsgData(db.getOrderById((String) (((Message) msg).GetMsgData())));
                        client.sendToClient(newmsg);
                        }
                        else{
                            controller.addtolog("Error Data Type");
                        }
                        break;
                    case UPDATEORDER:
                        if (((Message) msg).GetMsgData() instanceof Order) {
                            newmsg.SetMsgOpcodeValue(OpCodes.UPDATEORDER);
                            newmsg.SetMsgData(db.updateOrderById((Order)(((Message) msg).GetMsgData())));
                            client.sendToClient(newmsg);
                        }
                        else{
                            controller.addtolog("Error Data Type");
                        }
                        break;
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
//        System.exit(0);
    }

    @Override
    protected void clientConnected(ConnectionToClient client) {
        controller.addRow(client.getInetAddress().getHostName(), client.getInetAddress().getHostAddress());
    }

    @Override
    synchronized protected void clientDisconnected(
            ConnectionToClient client) {
        controller.addtolog(client.getInetAddress().getHostAddress() + " disconnected");
        controller.removeRowByIP(client.getInetAddress().getHostAddress());
    }


}
//End of EchoServer class
