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

    @RequestMapping(value = "/{id:(?!link|index|privateURL|404|info|expired).*[^_]$}", method = RequestMethod.GET)
    public Object redirectTo(@PathVariable String id,
                             @RequestParam(value="privateToken", required=false) String privateToken,
                             HttpServletResponse response, HttpServletRequest request,
                             Map<String, Object> model) {


        final Claims claims = (Claims) request.getAttribute("claims");
        System.out.println("Claims: " + claims);

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
            System.out.print(URIavailability.toString());
           if(!URIavailability.isAvailable()){
                // If the target URI is not available
                response.setStatus(HttpStatus.NOT_FOUND.value());
                Date date = new Date(URIavailability.getDate());
                model.put("target", shortURL.getTarget());
                model.put("date", date.toString());
                return "notAvailable";
           }

             //Else: Correct, redirect
              //simulation
            long current = System.currentTimeMillis();
            this.rabbitTemplate.convertAndSend(queue2,"66.249.66.106"+","+shortURL.getHash());
            long current2 = System.currentTimeMillis();
            current2 = (current2 - current);
            System.out.println(current2);
            return createSuccessfulRedirectToResponse(shortURL, response);


    }


    @RequestMapping(value = "/{id:(?!link|index|privateURL|404|info|expired).*_}", method = RequestMethod.GET)
    public Object redirectToWithUserList(@PathVariable String id,
                             @RequestParam(value="privateToken", required=false) String privateToken,
                             HttpServletResponse response, HttpServletRequest request,
                             Map<String, Object> model) {



        boolean authenticated = false;
        String userName = null;

        final Claims claims = (Claims) request.getAttribute("claims");
       //
        if(claims!=null){
            System.out.println("Claims: " + claims);
            userName = claims.getSubject();
            authenticated = true;
        }


        logger.info("Requested USER PROTECTED redirection with hash " + id + " - privateToken=" + privateToken+ " -loggedUser: " + userName);


        ShortURL shortURL = shortURLRepository.findByHash(id);
        System.out.println(shortURL);

        if(shortURL == null){
            //Is null, not found
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return "404";
        }
        else{
            if(!authenticated){
                //NOT AUTHENTICATED: Send to AngularJS login bridge
                System.out.println("INFO: NOT AUTHENTICATED, TO FRONTEND");
                return createLoginRedirect(id,request,response);
            }
            else{
                //AUTHENTICATED: Check if authorized
                System.out.println("INFO: AUTHENTICATED, CHECK IF AUTHORIZED");


                //todo: Que funcione, ahora esta fijo
                List<String> authorizedUsers = new ArrayList<String>();
                authorizedUsers.add("ismaro3");

                if(userName!=null && authorizedUsers.contains(userName)){
                    System.out.println("INFO: AUTHENTICATED, AUTHORIZED");
                    //Authorized, can proceed to next step
                    return redirectTo(id,privateToken,response,request,model);
                }
                else{
                    System.out.println("INFO: AUTHENTICATED, NOT AUTHORIZED");
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    model.put("hash",id);
                    //Tengo que mandarle al AngularJS... el que se encarga
                    return "forbidden";
                }
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


    protected RedirectView createLoginRedirect(String id, HttpServletRequest request,HttpServletResponse response) {

        RedirectView redirView = new RedirectView("http://" + request.getServerName() + ":" + request.getServerPort() + "/#/bridge/" + id);
        redirView.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
        return redirView;

    }

    protected String extractIP(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
}
