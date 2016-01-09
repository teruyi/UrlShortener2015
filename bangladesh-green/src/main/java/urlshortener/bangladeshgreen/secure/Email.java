package urlshortener.bangladeshgreen.secure;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import urlshortener.bangladeshgreen.domain.URIDisabled;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.List;
import java.util.Properties;


/**
 * Class used for sending Emails (all types).
 * It sends email from a (enterprise) GMail Account.
 */
public class Email{

    private static final Logger logger = LoggerFactory.getLogger(Email.class);

    @Value("${email.username}")
    private String username;

    @Value("${email.password}")
    private String password;
    private final String BEGIN = "We regret that the following links shortened by you have problems: \n";
    private final String DELAY ="  // The link has bad timeouts. \n";
     private final String SERVICE = " The link has low service time. \n";
    private final String SERVER_DOWN =  "The link are down for a long time. \n";
    private final String FIN =   "When the links stop having problems, we will communicate you their rehabilitation. Thanks for your attention.";
    private final String CONTINUE = "We are pleased to inform you that the links have been restored for not having problems.";

    private final Properties props = new Properties() {{
        put("mail.smtp.auth", "true");
        put("mail.smtp.starttls.enable", "true");
        put("mail.smtp.host", "smtp.gmail.com");
        put("mail.smtp.port", "587");
    }};

    private String destEmail;


    public Email(){

    }

    public void setDestination(String destEmail){
        this.destEmail = destEmail;
    }

    /**
     * Pre: ruta es un path valido.
     * Post: Crea un anuncio y lo envia a destEmail.
     */
    public void sendValidation(String title, String description, String link) {
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {

            // Creates the message.
            Message message = new MimeMessage(session);

            // Sets who sends the mail.
            message.setFrom(new InternetAddress(username));

            // Sets the destination.
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(destEmail));

            // Set the subject of the email.
            message.setSubject(title);

            // Creates the body of the message.
            BodyPart messageBodyPart = new MimeBodyPart();

            // Adds the text to the message body.
            messageBodyPart.setText(description);

            message.setText("Please validate your account in the following link: \n" + link +
                "\n\n If you don't know how this email has come to you, please contact us replying this email.");

            // Finally, sends the message.
            Transport.send(message);

            logger.info("Validation sent succesfully to " + destEmail);
        } catch (MessagingException e) {
            logger.info(e.toString());
        }
    }

    public void sendNotification(String title, String description, List<URIDisabled> enables,List<URIDisabled>disables) {
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {

            // Creates the message.
            Message message = new MimeMessage(session);

            // Sets who sends the mail.
            message.setFrom(new InternetAddress(username));

            // Sets the destination.
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(destEmail));

            // Set the subject of the email.
            message.setSubject(title);

            // Creates the body of the message.
            BodyPart messageBodyPart = new MimeBodyPart();

            // Adds the text to the message body.
            messageBodyPart.setText(description);
            String uris = "";
            if(enables.size()>0 || disables.size()>0) {
                if (enables.size() > 0) {
                    uris = CONTINUE + "\n";
                    for (URIDisabled a : enables) {
                        uris = uris + a + "\n";
                    }
                }
                if (disables.size() > 0) {
                    uris = BEGIN + "\n";
                    for (URIDisabled a : disables) {


                        if (a.getCause().compareTo("service") == 0) {
                            uris = uris + a.getTarget() + "    // " + SERVICE + "\n";
                        }
                        if (a.getCause().compareTo("delay") == 0) {
                            uris = uris + a.getTarget() + "    // " + DELAY+ "\n";
                        }
                        if (a.getCause().compareTo("down") == 0) {
                            uris = uris + a.getTarget() + "    // " + SERVER_DOWN+ "\n";
                        }

                    }
                }
            }
            uris = uris +"\n\n" +FIN;
            message.setText(uris);
                    // Finally, sends the message.
            Transport.send(message);

            logger.info("Delay notification send " + destEmail);
        } catch (MessagingException e) {
            logger.info(e.toString());
        }
    }

}
