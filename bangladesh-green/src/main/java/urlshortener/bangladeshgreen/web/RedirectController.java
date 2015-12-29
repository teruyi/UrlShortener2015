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

        logger.info("Requested redirection with hash " + id + " - privateToken=" + privateToken);

        URIAvailable URIavailability = null;
        ShortURL shortURL = shortURLRepository.findByHash(id);


            if(shortURL == null){
               //Is null, not found
               response.setStatus(HttpStatus.NOT_FOUND.value());
               return "404";
            }

            //Check if link is expired
            boolean hasExpired = hasExpired(shortURL);

            //Check if private token is required
            boolean isPrivate = shortURL.isPrivateURI();

            if(hasExpired){
                //Has expired
                response.setStatus(HttpStatus.GONE.value());
                return "expired";
            }

            if(isPrivate && (privateToken == null || !shortURL.getPrivateToken().equals(privateToken))){
                //Is private and no correct token has been supplied
                response.setStatus(HttpStatus.FORBIDDEN.value());
                model.put("hash", id);
                return "privateURL";
            }

            URIavailability = availableRepository.findByTarget(shortURL.getTarget());
           if(!URIavailability.isAvailable()){
                 // If the target URI is not available
                 response.setStatus(HttpStatus.GONE.value());
                 return "410";
             }

             //Else: Correct, redirect
              //simulation
            //this.rabbitTemplate.convertSendAndReceive(queue2,"66.249.66.106"+","+shortURL.getHash());
            return createSuccessfulRedirectToResponse(shortURL, response);


    }


    @RequestMapping(value = "/{id:(?!link|index|privateURL|404|info|expired).*}_", method = RequestMethod.GET)
    public Object redirectToWithUserList(@PathVariable String id,
                             @RequestParam(value="privateToken", required=false) String privateToken,
                             HttpServletResponse response, HttpServletRequest request,
                             Map<String, Object> model) {


        String userName = "ismaro3";
        //final Claims claims = (Claims) request.getAttribute("claims");
        //userName = claims.getSubject();


        logger.info("Requested USER PROTECTED redirection with hash " + id + " - privateToken=" + privateToken);


        ShortURL shortURL = shortURLRepository.findByHash(id);
        System.out.println(shortURL);

        if(shortURL == null){
            //Is null, not found
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return "404";
        }
        else{
            List<String> authorizedUsers = shortURL.getAuthorizedUsers();
            if(authorizedUsers.contains(userName)){
                //Authorized, can proceed to next step
                return redirectTo(id,privateToken,response,request,model);
            }
            else{
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return "403";
            }
        }



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

    protected String extractIP(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
}
