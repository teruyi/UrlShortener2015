package urlshortener.bangladeshgreen.web;

import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import urlshortener.bangladeshgreen.domain.ShortURL;
import urlshortener.bangladeshgreen.domain.URIAvailable;
import urlshortener.bangladeshgreen.repository.ClickRepository;
import urlshortener.bangladeshgreen.repository.ShortURLRepository;
import urlshortener.bangladeshgreen.repository.URIAvailableRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by ismaro3.
 */
@Controller
public class RedirectController {


    private static final Logger log = LoggerFactory
            .getLogger(RedirectController.class);

    private static final Logger logger = LoggerFactory.getLogger(RedirectController.class);

    private static final String queue2 = "locaQueue1";

    @Autowired
    protected ShortURLRepository shortURLRepository;

    @Autowired
    protected ClickRepository clickRepository;

    @Autowired
    protected URIAvailableRepository availableRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RequestMapping(value = "/{id:(?!link|index|privateURL|404|info|expired).*}", method = RequestMethod.GET)
    public Object redirectTo(@PathVariable String id,
                             @RequestParam(value="privateToken", required=false) String privateToken,
                             HttpServletResponse response, HttpServletRequest request,
                             Map<String, Object> model) {


        String userName = null; //Currently logged-in user username
        boolean authenticated = false;

        //Get authentication information if present
        final Claims claims = (Claims) request.getAttribute("claims");
        if(claims!=null){
            userName = claims.getSubject();
            authenticated = true;
        }


        URIAvailable URIavailability = null;
        ShortURL shortURL = shortURLRepository.findByHash(id);



        if(shortURL == null){
            //Is null, not found
            logger.info("Requested non-existent hash " + id);
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return "404";
        }

        //Check if link is expired
        boolean hasExpired = hasExpired(shortURL);

        //Check if private token is required
        boolean isPrivate = shortURL.isPrivateURI();

        //Check if the link is user-protected
        boolean isUserProtected = shortURL.getAuthorizedUsers()!=null && shortURL.getAuthorizedUsers().size()>0;


        logger.info("Requested redirection with hash " + id +
                " (private="+ isPrivate +
                ", hasExpiration="+ (shortURL.getExpirationSeconds()!=null)+
                ", isUserProtected=" + isUserProtected + ") - (privateToken=" + privateToken + ", hasExpired=" + hasExpired + " ,currentUser=" + userName + ")");



        //FIRST CHECK: If expired, redirect to expired.jsp
            if(hasExpired){
                //Has expired
                response.setStatus(HttpStatus.GONE.value());
                return "expired";
            }

            //SECOND CHECK: If user protected, check if user is authenticated and has permissions.
            if (isUserProtected) {
                if(!authenticated){
                    //NOT AUTHENTICATED: Send to AngularJS login bridge
                    return createLoginRedirect(id,request,response);
                }
                else{
                    //AUTHENTICATED: Check if it is not authorized

                    //todo: Que funcione, ahora esta fijo
                    List<String> authorizedUsers = shortURL.getAuthorizedUsers();
                    //authorizedUsers.add("ismaro3");

                    if(userName==null || !authorizedUsers.contains(userName)){
                        //Not authorized -> send to forbidden page
                        response.setStatus(HttpStatus.FORBIDDEN.value());
                        model.put("hash",id);
                        return "forbidden";
                    }

                }
            }


            //THIRD CHECK: If it requires token and is not proviced, redirect to "privateURL.jsp".
            if(isPrivate && (privateToken == null || !shortURL.getPrivateToken().equals(privateToken))){
                //Is private and no correct token has been supplied
                response.setStatus(HttpStatus.FORBIDDEN.value());
                model.put("hash", id);
                return "privateURL";
            }


            //FORTH CHECK: If the URI is not available (since last check), go to "notAvailable.jsp".
            URIavailability = availableRepository.findByTarget(shortURL.getTarget());
            System.out.print(URIavailability.toString());
             if(!URIavailability.isAvailable()){
                // If the target URI is not available
                response.setStatus(HttpStatus.NOT_FOUND.value());
                Date date = new Date(URIavailability.getDate());
                model.put("target", shortURL.getTarget());
                model.put("date", date.toString());
                return "notAvailable";
             }


            //ALL RIGHT, proceed to redirect

            //Add IP and hash information
            this.rabbitTemplate.convertAndSend(queue2,"66.249.66.106"+","+shortURL.getHash());

            //Redirect
            return createSuccessfulRedirectToResponse(shortURL, response);


    }



    /**
     * Returns true if link has expired.
     * False otherwise.
     * @param shortURL
     * @return
     */
    private boolean hasExpired(ShortURL shortURL){
        final long ONE_SECOND_IN_MILLIS = 1000;

        Long expirationSeconds = shortURL.getExpirationSeconds();
        if(expirationSeconds!=null){
            Date creationDate = shortURL.getCreated();
            Date expirationDate = new Date(creationDate.getTime() + expirationSeconds*ONE_SECOND_IN_MILLIS);

            long expirationDateMillis = expirationDate.getTime();
            Calendar date = Calendar.getInstance();
            long currentTimeMillis = date.getTimeInMillis();

            return currentTimeMillis > expirationDateMillis;

        }
        else{
            return false;
        }
    }




    protected RedirectView createSuccessfulRedirectToResponse(ShortURL l, HttpServletResponse response) {

        RedirectView redirView = new RedirectView(l.getTarget());
        redirView.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
        return redirView;

    }


    protected RedirectView createLoginRedirect(String id, HttpServletRequest request,HttpServletResponse response) {

        RedirectView redirView = new RedirectView("http://" + request.getServerName() + ":" + request.getServerPort() + "/#/bridge/" + id);
        redirView.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
        return redirView;

    }

    protected String extractIP(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
}
