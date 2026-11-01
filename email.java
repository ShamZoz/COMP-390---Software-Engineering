// Libraries for sending an email ---> ERROR IS HERE!!! The libraries aren't recognized despite
// vscode saying the functions are and the program still runs despite them being underlined in red?
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class email extends GradeActivity {
    private final JFrame frame = new JFrame("Mini Blackboard");

    // Message feature
    // Main References used: 
    // https://www.geeksforgeeks.org/java/send-email-using-java-program/
    // https://stackoverflow.com/questions/884943/how-do-i-send-an-e-mail-in-java
    // https://www.geeksforgeeks.org/java/sending-email-java-ssltls-authentication/
    // https://mailtrap.io/blog/java-send-email/


    
    private String ask(String message) {
        String s = JOptionPane.showInputDialog(frame, message);
        return s == null || s.trim().isEmpty() ? null : s.trim();
    }



    public void sendEmail(){

        // Get information
        String emailSender = ask("Please enter your email: ");
        String emailPassword = ask("Please enter your password: ");
        String host = "localhost";
        String emailReciever = ask("Recipiant address:");
        String emailSubject = ask("Email Subject? ");
        String emailMessage = ask("Email Message: ");

        // Get the SMTP (Simple Mail Transfer Protocol host) properties. This includes the host name and port
        // Alternatively, you can email with smtp.gmail.com on the same port
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.office365.com");
        props.put("mail.smtp.port", "587");


        // Authenticate session
        // https://docs.oracle.com/javaee/7/api/javax/mail/Session.html
        // https://www.geeksforgeeks.org/java/session-management-in-java/
        // https://docs.oracle.com/javase/8/docs/api/java/net/PasswordAuthentication.html

        // PasswordAuthentication takes a string and a char array, so we must convert emailpassword
        // to a char array
        // https://www.w3schools.com/java/ref_string_tochararray.asp
        char[] password = emailPassword.toCharArray();

        
        // Current session creation has issues with authentication. An altnerative is provided below
                    //Session session = Session.getInstance(props, new Authenticator() {
                    //protected PasswordAuthentication getPasswordAuthentication() {

                    //    return new PasswordAuthentication(emailSender, password);
                    //}

                //});

        // Use this one instead      
        Session session = Session.getDefaultInstance(props); 
        

        // compose the message
        try {

            // javax.mail.internet.MimeMessage class is mostly used for abstraction,
            // which is the concept of hiding unnecessary, complex details from user
            MimeMessage message = new MimeMessage(session); 


            // header field of the header.
            message.setFrom(new InternetAddress(emailSender)); 
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(emailReciever));
            message.setSubject(emailSubject);
            message.setText(emailMessage);

            // Send message
            Transport.send(message);
            System.out.println("Email successfully sent!");
        }

        // Error handeling: MessagingException is "the base class for all exceptions thrown by the Messaging classes"
        // https://docs.oracle.com/javaee/7/api/javax/mail/MessagingException.html
        catch (MessagingException mex) {
            mex.printStackTrace();
        }

    }


}
