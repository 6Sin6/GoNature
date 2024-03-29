package GoNatureServer;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * This class is responsible for sending emails using Gmail's SMTP server.
 * It contains a single public static method, sendEmail, which sends an email to a specified recipient.
 */
public class GmailSender {

    /**
     * Sends an email to the specified recipient using Gmail's SMTP server.
     * This method sets up the SMTP server properties, creates a Session object, and then creates and sends a MimeMessage.
     * If the message is successfully sent, it prints a success message to the console.
     * If an error occurs while sending the message, it prints the stack trace of the exception to the console.
     *
     * @param to The email address of the recipient.
     * @param Subject The subject of the email.
     * @param textMessage The body of the email.
     */
    public static void sendEmail(String to, String Subject, String textMessage) {

        // Set up the SMTP server properties
        Properties p = new Properties();
        p.put("mail.smtp.auth", "true");
        p.put("mail.transport.protocol", "smtp");
        p.put("mail.smtp.host", "smtp.gmail.com");
        p.put("mail.smtp.port", "587");
        p.put("mail.smtp.starttls.enable", "true");
        p.put("mail.smtp.starttls.required", "true");

        // Create a Session object
        Session session = Session.getInstance(p,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("GoNatureProjectGroup5@gmail.com", "vtucspkudgwarovg");
                    }
                });

        try {
            // Create a default MimeMessage object
            MimeMessage message = new MimeMessage(session);

            // Set From: header field
            message.setFrom(new InternetAddress("GoNatureProjectGroup5@gmail.com"));

            // Set To: header field
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject(Subject);

            // Set the actual message
            message.setText(textMessage);

            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}