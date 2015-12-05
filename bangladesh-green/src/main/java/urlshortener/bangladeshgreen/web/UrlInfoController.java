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
import org.springframework.web.servlet.ModelAndView;
import urlshortener.bangladeshgreen.domain.Click;
import urlshortener.bangladeshgreen.domain.InfoURL;
import urlshortener.bangladeshgreen.domain.ShortURL;
import urlshortener.bangladeshgreen.repository.ClickRepository;
import urlshortener.bangladeshgreen.repository.ShortURLRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

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

/*
    @RequestMapping(value = "/{id:(?!link|index|info).*}+", method = RequestMethod.GET , produces ="text/html")
    public String exception2(Model model)
    {
        return "redirect:/resources/static/info.jsp";
        //return "redirect:/resources/info.jsp";
    }

*/
    @RequestMapping(value = "/{id:(?!link|index|info).*}+", method = RequestMethod.GET , produces ="text/html")
    public ModelAndView exception2(@PathVariable String id, HttpServletRequest request)
    {
        ModelAndView modelAndView;
        ShortURL l = shortURLRepository.findByHash(id);
        int count = clickRepository.findByHash(id).size();
        try {
            modelAndView = new ModelAndView("redirect:/info.jsp");
            modelAndView.addObject("target",l.getTarget());
            modelAndView.addObject("date",l.getCreated());
            modelAndView.addObject("count",count);

        } catch(IndexOutOfBoundsException e) {
            modelAndView = handleException();
        }
        return modelAndView;
    }

    private void generateException(){
        throw new IndexOutOfBoundsException();
    }

    private ModelAndView handleException(){
        return new ModelAndView("404.jsp");
    }

    /*@RequestMapping(value = "/{id:(?!link|index|info|privateURL).*}+", method = RequestMethod.GET , produces ="text/html")
    public ResponseEntity<?> redirectTo(@PathVariable String id, HttpServletRequest request,
                                        HttpServletResponse response) {
        logger.info("Redirect to HTML: " + id);
        ShortURL l = shortURLRepository.findByHash(id);
        if (l != null) {
            InfoURL info = new InfoURL(l.getTarget(), l.getCreated().toString(), extractUsesCount(l.getHash()));
            return createSuccessfulRedirectToResponse(l, request, response);
        } else {
            logger.info("Empty URL " + id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
*/

    @RequestMapping(value = "/{id:(?!link|index).*}+", method = RequestMethod.GET , produces ="application/json")
    public ResponseEntity<?> sendJson(@PathVariable String id, HttpServletRequest request) {
        logger.info("Link info json: " + id);
        ShortURL l = shortURLRepository.findByHash(id);
        if (l != null) {
            InfoURL info = new InfoURL(l.getTarget(), l.getCreated().toString(), extractUsesCount(l.getHash()));
            return new ResponseEntity<>(info, HttpStatus.OK);
        } else {
            logger.info("Empty URL " + id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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

    protected ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l,HttpServletRequest request,
                                                                   HttpServletResponse response) {
        response.setStatus(HttpStatus.SEE_OTHER.value());
        try {
            request.getRequestDispatcher("info.jsp").forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.SEE_OTHER);
    }

}
