// This program allows emails to be sent from within the program

// Current Status: WIP ---> Caused by: sun.security.validator.ValidatorException: PKIX path 
// building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid 
// certification path to requested target

// Main References used: 
    // https://www.geeksforgeeks.org/java/send-email-using-java-program/
    // https://stackoverflow.com/questions/884943/how-do-i-send-an-e-mail-in-java
    // https://www.geeksforgeeks.org/java/sending-email-java-ssltls-authentication/
    // https://mailtrap.io/blog/java-send-email/

// Libraries for sending an email ---> Despite their red underlines, they are functional
import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Authenticator;

public class email extends GradeActivity {


    public void sendEmail(){

        // Get information
        String emailSender = ask("Please enter your email address: ");
        String emailPassword = ask("Please enter your password: ");
        String host = "localhost";
        String emailReciever = ask("Recipiant address:");
        String emailSubject = ask("Email Subject? ");
        String emailMessage = ask("Email Message: ");


        // Set up the SMTP (Simple Mail Transfer Protocol host) server.
        java.util.Properties props = new java.util.Properties();


        // Connect to SMTP, in this case outlook (The server the email is being sent through)
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp-mail.outlook.com");
        props.put("mail.smtp.port", "587");


        // OLD SESSION CREATOR --> Errors due to lack of authentication
        //Session session = Session.getDefaultInstance(props, null);

        // Sources:
        // https://docs.oracle.com/javaee/7/api/javax/mail/Session.html
        // https://www.geeksforgeeks.org/java/session-management-in-java/
        // https://docs.oracle.com/javase/8/docs/api/java/net/PasswordAuthentication.html

        
        // Create the Session object and authenticate it using your password
        Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {

                    return new PasswordAuthentication(emailSender, emailPassword);
                }

            });

        // compose the message
        try {

            // Create a message
            Message message = new MimeMessage(session); 


            // Fill email with sender, recipient, subject, and the text
            message.setFrom(new InternetAddress(emailSender)); 
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(emailReciever));
            message.setSubject(emailSubject);
            message.setText(emailMessage);



            // Send message ---> This is where jakarta.activation is needed to keep prevent an error
            Transport.send(message);
            System.out.println("Email successfully sent!");

            //Instead, print to console
            //System.out.println("Sender " + emailSender);
            //System.out.println("To " + emailReciever);
            //System.out.println("Subject " + emailSubject);
            //System.out.println(emailMessage);
        }



        // Error handeling: MessagingException is "the base class for all exceptions thrown by the Messaging classes"
        // https://docs.oracle.com/javaee/7/api/javax/mail/MessagingException.html
        catch (MessagingException mex) {
            mex.printStackTrace();
        }

    }
    
    // Test the method
    public static void main(String[] args) {
        email myEmail = new email();
        myEmail.sendEmail();
    }


}
