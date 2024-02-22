// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 
package Server;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Vector;

import gui.ServerPortFrameController;
import logic.Faculty;
import logic.Student;
import OCSF.*;


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

public class EchoServer extends AbstractServer {
    //Class variables *************************************************
    private Connection conn;

    private ServerPortFrameController controller;

    //Constructors ****************************************************

    /**
     * Constructs an instance of the echo server.
     *
     * @param port The port number to connect on.
     */

    public EchoServer(int port, ServerPortFrameController controller) {
        super(port);
        this.controller=controller;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            this.controller.addtolog("Driver definition succeed");
        } catch (Exception ex) {
            /* handle the error*/
            this.controller.addtolog("Driver definition failed");
        }

        try {
            this.conn = DriverManager.getConnection("jdbc:mysql://"+controller.getURLComboBox()+":3306/test?serverTimezone=IST", controller.getUserName(), controller.getPassword());
            this.controller.addtolog("SQL connection succeed");
        } catch (SQLException ex) {/* handle any errors*/
            this.controller.addtolog("SQLException: " + ex.getMessage());
            this.controller.addtolog("SQLState: " + ex.getSQLState());
            this.controller.addtolog("VendorError: " + ex.getErrorCode());
        }
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

    private void updateTODB(ArrayList<String> msg) throws SQLException {
        if (msg == null || msg.size() < 4) {
            throw new IllegalArgumentException("The input list must contain at least 4 elements.");
        }

        // Assuming the SQL table columns are named id, column1, column2, column3,
        // and you're updating column1, column2, and column3 based on id.
        String sql = "UPDATE students SET name = ?, lastname = ?, faculty = ? WHERE idstudents = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set the values from your list to the PreparedStatement
            pstmt.setString(1, msg.get(1)); // Set column1
            pstmt.setString(2, msg.get(2)); // Set column2
            pstmt.setString(3, msg.get(3)); // Set column3
            pstmt.setString(4, msg.get(0)); // Set id for the WHERE clause

            // Execute the update
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                this.controller.addtolog("Updated DB successfully!");
            } else {
                this.controller.addtolog("No rows affected. Check if the ID exists.");
            }
        } catch (SQLException e) {
            this.controller.addtolog("Update DB encountered an error: " + e.getErrorCode());
            throw e;
        }
    }

    private Student GetStu(String msg) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM students WHERE idstudents = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, msg); // Set the student ID in the query

            rs = pstmt.executeQuery();

            // Check if a student was found
            if (rs.next()) {
                // Assuming the students table has an 'idstudents' and 'name' columns
                String idstudents = rs.getString("idstudents");
                String name = rs.getString("name");
                String lastname = rs.getString("lastname");
                String fc = rs.getString("faculty");
                // Create and return a Student object
                return new Student(idstudents, name, lastname, new Faculty(fc, "9901000"));
            } else {
                // Handle the case where no student was found
                this.controller.addtolog("No student found with ID: " + msg);
                return null; // Or throw a custom exception
            }
        } catch (SQLException e) {
            this.controller.addtolog("Encountered an error: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) { /* ignored */ }
            if (pstmt != null) try {
                pstmt.close();
            } catch (SQLException e) { /* ignored */ }
        }
    }


    public void handleMessageFromClient
            (Object msg, ConnectionToClient client) throws SQLException {
        if (msg instanceof String) {
            Student s = GetStu(String.valueOf(msg));
            if (s != null) {
                this.controller.addtolog("Server Found");
                this.sendToAllClients(s.toString());
            } else {
                this.controller.addtolog("Not Found");
                this.sendToAllClients("Error");
            }
        }
        else if (msg instanceof ArrayList<?>) {
            this.controller.addtolog("Message received from " + client);
            for (String s : (ArrayList<String>) msg) {
                System.out.print(s + " ");
            }
            this.controller.addtolog("");
            try {
                updateTODB((ArrayList<String>) msg);
//                String newUpdatedStu="";
//                for (String s : (ArrayList<String>) msg) {
//                    newUpdatedStu+=s+" ";
//                }
                this.sendToAllClients("Student Updated Successfully");
            } catch (SQLException e) {
                // Prepare and send a message back to the client about the duplicate entry
                String errorMessage = "Could not Update User Details.";
                this.sendToAllClients(errorMessage);
            }
        } else this.controller.addtolog("Message Error!");
    }

    /**
     * This method overrides the one in the superclass.  Called
     * when the server stops listening for connections.
     */
    protected void serverStopped() {
        this.controller.addtolog("Server has stopped listening for connections.");
    }
}
//End of EchoServer class
