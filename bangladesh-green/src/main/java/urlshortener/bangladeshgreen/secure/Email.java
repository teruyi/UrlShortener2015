package urlshortener.bangladeshgreen.secure;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import urlshortener.bangladeshgreen.domain.URIAvailable;
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
    private final String NOTIFY = "Some of your links Have availability " +
            "issues If problems persist ," +
            " links will be disabled again Until They work If time" +
            " passes and Certain links are not recovered ," +
            " They Will be Affected The links are deleted \n";

    private final String DELAY ="   The link has bad timeouts. \n";
     private final String SERVICE = "   The link has low service time. \n";
    private final String SERVER_DOWN =  "   The link are down for a long time. \n";

    private final String DISABLE = "Some links that were having problems" +
            " have not improved" +
            " its availability," +
            " so we have proceed to disable them. If the problem persist," +
            " they will be deleted." +
            " The affected  links are:\n";

    private final String DELETE =  "Sorry, but some of your links were deleted from" +
            " our system because of" +
            " its bad availability." + " We noticed you two times before this one." +
            " The deleted links are:\n";

    private final String ENABLE = "We are glad to inform you that some of your" +
            " links" +
            " have been re-enabled. They were disabled because of their bad availability." +
            " These links are:\n";

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

    public void sendNotification(String title, String description, List<URIAvailable> stateOne, List<URIAvailable>stateTwo,List<URIAvailable>stateThree,List<URIAvailable>stateFour) {
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
            // First the notify urls
            if(stateOne.size() >0){
                uris = uris + ENABLE;
                for(URIAvailable a : stateOne){

                    uris = uris + a.getTarget() + "\n";
                }
            }

            if(stateTwo.size() >0){
                uris = uris + NOTIFY;
                for(URIAvailable a : stateTwo){
                    String problem = a.getProblem();
                    if(problem.compareTo("down") == 0){
                        uris = uris + a.getTarget()  + SERVER_DOWN + "\n";
                    }
                    if(problem.compareTo("delay") == 0){
                        uris = uris + a.getTarget() +  DELAY+ "\n";
                    }
                    if(problem.compareTo("service") == 0){
                        uris = uris + a.getTarget() +  SERVICE+ "\n";
                    }
                }
            }

            if(stateThree.size() > 0){
                uris = uris + DISABLE;
                for(URIAvailable a : stateThree){
                    String problem = a.getProblem();
                    if(problem.compareTo("down") == 0){
                        uris = uris + a.getTarget() +  SERVER_DOWN+ "\n";
                    }
                    if(problem.compareTo("delay") == 0){
                        uris = uris + a.getTarget() + DELAY+ "\n";
                    }
                    if(problem.compareTo("service") == 0){
                        uris = uris + a.getTarget() + SERVICE+ "\n";
                    }
                }
            }
            if(stateFour.size() > 0){
                uris = uris + DELETE;
                for(URIAvailable a : stateFour){
                    String problem = a.getProblem();
                    if(problem.compareTo("down") == 0){
                        uris = uris + a.getTarget()  + SERVER_DOWN+ "\n";
                    }
                    if(problem.compareTo("delay") == 0){
                        uris = uris + a.getTarget() + DELAY+ "\n";
                    }
                    if(problem.compareTo("service") == 0){
                        uris = uris + a.getTarget() +  SERVICE+ "\n";
                    }
                }

            }
            message.setText(uris);
                    // Finally, sends the message.
            Transport.send(message);

            logger.info("Delay notification send " + destEmail);
        } catch (MessagingException e) {
            logger.info(e.toString());
        }
    }

}
