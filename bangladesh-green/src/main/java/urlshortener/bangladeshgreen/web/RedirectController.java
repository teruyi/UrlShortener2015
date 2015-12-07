package urlshortener.bangladeshgreen.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import urlshortener.bangladeshgreen.domain.Click;
import urlshortener.bangladeshgreen.domain.ShortURL;
import urlshortener.bangladeshgreen.repository.ClickRepository;
import urlshortener.bangladeshgreen.repository.ShortURLRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

/**
 * Created by ismaro3.
 */
@Controller
public class RedirectController {


    private static final Logger log = LoggerFactory
            .getLogger(RedirectController.class);

    private static final Logger logger = LoggerFactory.getLogger(RedirectController.class);

    @Autowired
    protected ShortURLRepository shortURLRepository;

    @Autowired
    protected ClickRepository clickRepository;

    @RequestMapping(value = "/{id:(?!link|index|privateURL|404|info).*}", method = RequestMethod.GET)
    public Object redirectTo(@PathVariable String id,
                             @RequestParam(value="privateToken", required=false) String privateToken,
                             HttpServletResponse response, HttpServletRequest request,
                             Map<String, Object> model) {

        logger.info("Requested redirection with hash " + id + " - privateToken=" + privateToken);
        ShortURL l = shortURLRepository.findByHash(id);


        if (l != null) {

            if(l.isPrivateURI() && ( privateToken ==null || !l.getPrivateToken().equals(privateToken))){
                //If private and incorrect token, then unauthorized
                response.setStatus(HttpStatus.FORBIDDEN.value());
                model.put("hash",id);
                return "privateURL";
            }
            else{
                createAndSaveClick(id, extractIP(request));
                return createSuccessfulRedirectToResponse(l,response);
            }
        } else {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return "404";

        }
    }


    protected RedirectView createSuccessfulRedirectToResponse(ShortURL l, HttpServletResponse response) {

        RedirectView redirView = new RedirectView(l.getTarget());
        redirView.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
        return redirView;

    }


    protected void createAndSaveClick(String hash, String ip) {
        Click cl = new Click(hash, new Date(),ip);
        cl=clickRepository.save(cl);
        log.info(cl!=null?"["+hash+"] saved with date ["+cl.getDate()+"]":"["+hash+"] was not saved");
    }

    protected String extractIP(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
}
