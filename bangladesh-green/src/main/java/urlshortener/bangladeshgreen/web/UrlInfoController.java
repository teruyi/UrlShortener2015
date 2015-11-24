package urlshortener.bangladeshgreen.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import urlshortener.bangladeshgreen.domain.Click;
import urlshortener.bangladeshgreen.domain.InfoURL;
import urlshortener.bangladeshgreen.domain.ShortURL;
import urlshortener.bangladeshgreen.repository.ClickRepository;
import urlshortener.bangladeshgreen.repository.ShortURLRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by teruyi on 23/11/15.
 */
/**
 * Controller used for show URL info.
 * When a user wants to get info about an URL, the user make a get request with the URL followed by '+' and
 * the controller return a JSON or HTML redirect with the Information.
 */
@RestController
public class UrlInfoController {
    private static final Logger log = LoggerFactory
            .getLogger(UrlShortenerController.class);

    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerController.class);

    @Autowired
    protected ShortURLRepository shortURLRepository;

    @Autowired
    protected ClickRepository clickRepository;

    @RequestMapping(value = "/{id:(?!link|index).*}+", method = RequestMethod.GET)
    public ResponseEntity<?> redirectTo(@PathVariable String id,
                                        HttpServletRequest request) {
        logger.info("Link info: " + id);
        ShortURL l = shortURLRepository.findByHash(id);
        InfoURL info = new InfoURL(l.getTarget(), l.getCreated().toString(), extractUsesCount(l.getHash()));
        if (l != null) {
            return new ResponseEntity<>(info, HttpStatus.ACCEPTED);
        } else {
            logger.info("Empty URL " + id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    protected int extractUsesCount(String hash) {
        int n = 0;
        logger.info(""+clickRepository.list().size());
        List<Click> c = clickRepository.list();
        for (Click click : c){
            logger.info(click.getHash() +"  "+ hash);
            System.out.print(click.getHash() +"  "+ hash);
            if (click.getHash().compareTo(hash) == 0){
                n++;
            }
        }
        return n;
    }

}
