package urlshortener.bangladeshgreen.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import urlshortener.bangladeshgreen.domain.Click;
import urlshortener.bangladeshgreen.domain.InfoURL;
import urlshortener.bangladeshgreen.domain.ShortURL;
import urlshortener.bangladeshgreen.repository.ClickRepository;
import urlshortener.bangladeshgreen.repository.ShortURLRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Created by teruyi on 23/11/15.
 */
/**
 * Controller used for show URL info.
 * When a user wants to get info about an URL, the user make a get request with the URL followed by '+' and
 * the controller return a JSON or HTML redirect with the Information.
 */
@Controller
public class UrlInfoController {
    private static final Logger log = LoggerFactory
            .getLogger(UrlShortenerController.class);

    private static final Logger logger = LoggerFactory.getLogger(UrlInfoController.class);

    @Autowired
    protected ShortURLRepository shortURLRepository;

    @Autowired
    protected ClickRepository clickRepository;


    @RequestMapping(value = "/{id:(?!link|index|info).*}+", method = RequestMethod.GET , produces ="text/html")
    public Object sendHtml(@PathVariable String id,
                             @RequestParam(value="privateToken", required=false) String privateToken,
                             HttpServletResponse response, HttpServletRequest request,
                             Map<String, Object> model) {

        logger.info("Requested Link Info URL HTML with hash " + id + " - privateToken=" + privateToken);
        ShortURL l = shortURLRepository.findByHash(id);
        int count = clickRepository.findByHash(id).size();


        if (l != null) {

            if(l.isPrivateURI() && ( privateToken ==null || !l.getPrivateToken().equals(privateToken))){
                //If private and incorrect token, then unauthorized
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return "privateURL";
            }
            else{
                response.setStatus(HttpStatus.SEE_OTHER.value());
                model.put("url",l.getUri());
                model.put("target",l.getTarget());
                model.put("date",l.getCreated());
                model.put("count",count);
                return "info";
            }
        } else {
            logger.info("Empty URL " + id);
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return "404";

        }
    }


    @RequestMapping(value = "/{id:(?!link|index).*}+", method = RequestMethod.GET , produces ="application/json")
    public Object sendJson(@PathVariable String id,
                                      @RequestParam(value="privateToken", required=false) String privateToken,
                                      HttpServletResponse response, HttpServletRequest request,
                                      Map<String, Object> model) {

        logger.info("Requested Link Info URL JSON with hash " + id + " - privateToken=" + privateToken);
        ShortURL l = shortURLRepository.findByHash(id);
        int count = clickRepository.findByHash(id).size();
        if (l != null) {
            if(l.isPrivateURI() && ( privateToken ==null || !l.getPrivateToken().equals(privateToken))){
                //If private and incorrect token, then unauthorized
                response.setStatus(HttpStatus.FORBIDDEN.value());

                return "privateURL";
            }
            else{
                InfoURL info = new InfoURL(l.getTarget(), l.getCreated().toString(), count);
                response.setStatus(HttpStatus.OK.value());
                return new ResponseEntity<>(info, HttpStatus.OK);
            }
        } else {
            logger.info("Empty URL " + id);
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return "404";
        }
    }

    /**
     * Return number usesCounter
     */
    protected int extractUsesCount(String hash) {
        int n = 0;
        List<Click> c = clickRepository.list();
        for (Click click : c){
            if (click.getHash().compareTo(hash) == 0){
                n++;
            }
        }
        return n;
    }


}
