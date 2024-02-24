// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import CommonClientUI.ChatIF;

import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient {
    //Instance variables **********************************************

    /**
     * The interface type variable.  It allows the implementation of
     * the display method in the client.
     */
    ChatIF clientUI;
    public static boolean awaitResponse = false;

    //Constructors ****************************************************

    /**
     * Constructs an instance of the chat client.
     *
     * @param host     The server to connect to.
     * @param port     The port number to connect on.
     * @param clientUI The interface type variable.
     */

    public ChatClient(String host, int port, ChatIF clientUI)
            throws IOException {
        super(host, port);
        this.clientUI = clientUI;
    }

    //Instance methods ************************************************

    /**
     * This method handles all data that comes in from the server.
     *
     * @param msg The message from the server.
     */
    public void handleMessageFromServer(Object msg) throws IOException {
        System.out.println("--> handleMessageFromServer");
        if (msg instanceof String) {
            clientUI.display((String) msg);
            if (msg.equals("Disconnect")) {
                closeConnection();
            }
            awaitResponse = false;
        }
    }

        /**
         * This method handles all data coming from the UI
         *
         * @param message The message from the UI.
         */

        public void handleMessageFromClientUI (Object message) throws IOException {
            try {
                openConnection();//in order to send more than one message
                awaitResponse = true;
                sendToServer(message);
                // wait for response
                while (awaitResponse) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                clientUI.display("Could not send message to server: Terminating client." + e);
                quit();
            }
        }


        /**
         * This method terminates the client.
         */
        public void quit() throws IOException {
            closeConnection();
            System.exit(0);
        }
    }

//End of ChatClient class
