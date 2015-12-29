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
import urlshortener.bangladeshgreen.domain.ClickAdds;
import urlshortener.bangladeshgreen.domain.InfoURL;
import urlshortener.bangladeshgreen.domain.ShortURL;
import urlshortener.bangladeshgreen.domain.messages.ErrorResponse;
import urlshortener.bangladeshgreen.domain.messages.SuccessResponse;
import urlshortener.bangladeshgreen.repository.ClickRepository;
import urlshortener.bangladeshgreen.repository.ShortURLRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by teruyi.
 */
/**
 * Controller used for show URL info.
 * When a user wants to get info about an URL, the user make a get request with the URL followed by '+' and
 * the controller return a JSON or HTML redirect with the Information.
 */
@Controller
public class UrlInfoController {

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
                model.put("hash",id + "+");
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
            if(l.isPrivateURI() && (privateToken == null || !l.getPrivateToken().equals(privateToken))){
                //If private and incorrect token, then unauthorized
                response.setStatus(HttpStatus.FORBIDDEN.value());
                ErrorResponse error = new ErrorResponse("This link is private");
                return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
            }
            else{
                InfoURL info = new InfoURL(l.getTarget(), l.getCreated().toString(), count);
                SuccessResponse success = new SuccessResponse(info);
                response.setStatus(HttpStatus.OK.value());
                return new ResponseEntity<>(success, HttpStatus.OK);
            }
        } else {
            logger.info("Empty URL " + id);
            response.setStatus(HttpStatus.NOT_FOUND.value());
            ErrorResponse error = new ErrorResponse("URL not found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

    private List<ClickAdds> listByRegion(Date desde, Date hasta) {
        List<Click> list = clickRepository.findAll();
        List<ClickAdds> listt = new ArrayList<ClickAdds>();

        HashMap<String, Integer> names = new HashMap<String, Integer>();
        int indice = 0;
        for (Click a : list) {
            if(desde !=null  && hasta!= null) {
                if (a.getDate().after(desde) && a.getDate().before(hasta)) {

                    if (names.containsKey(a.getRegionName())) {
                        names.replace(a.getRegionName(), names.get(a.getRegionName()) + 1);
                    } else {
                        names.put(a.getRegionName(), 1);
                    }
                }
            }else if (desde == null && hasta !=null){
                if(a.getDate().before(hasta)){
                    if (names.containsKey(a.getRegionName())) {
                        names.replace(a.getRegionName(), names.get(a.getRegionName()) + 1);
                    } else {
                        names.put(a.getRegionName(), 1);
                    }
                }
            }
            else if(desde !=null && hasta ==null){
                if(a.getDate().after(desde)){
                    if (names.containsKey(a.getRegionName())) {
                        names.replace(a.getRegionName(), names.get(a.getRegionName()) + 1);
                    } else {
                        names.put(a.getRegionName(), 1);
                    }
                }
            }
            else{return null;}
        }
        Set keys = names.keySet();
        Iterator iterator=keys.iterator();
        while(iterator.hasNext()){
            String key = (String)iterator.next();
            ClickAdds aux = new ClickAdds(key,names.get(key));
            listt.add(aux);
        }
        return listt;
    }
    private List<ClickAdds> listByCity(Date desde, Date hasta) {
        List<Click> list = clickRepository.findAll();
        List<ClickAdds> listt = new ArrayList<ClickAdds>();

        HashMap<String, Integer> names = new HashMap<String, Integer>();
        int indice = 0;
        for (Click a : list) {
            if(desde !=null  && hasta!= null) {
                if (a.getDate().after(desde) && a.getDate().before(hasta)) {

                    if (names.containsKey(a.getCity())) {
                        names.replace(a.getCity(), names.get(a.getCity()) + 1);
                    } else {
                        names.put(a.getCity(), 1);
                    }
                }
            }else if (desde == null && hasta !=null){
                if(a.getDate().before(hasta)){
                    if (names.containsKey(a.getCity())) {
                        names.replace(a.getCity(), names.get(a.getCity()) + 1);
                    } else {
                        names.put(a.getCity(), 1);
                    }
                }
            }
            else if(desde !=null && hasta ==null){
                if(a.getDate().after(desde)){
                    if (names.containsKey(a.getCity())) {
                        names.replace(a.getCity(), names.get(a.getCity()) + 1);
                    } else {
                        names.put(a.getCity(), 1);
                    }
                }
            }else{return null;}
        }
        Set keys = names.keySet();
        Iterator iterator=keys.iterator();
        while(iterator.hasNext()){
            String key = (String)iterator.next();
            ClickAdds aux = new ClickAdds(key,names.get(key));
            listt.add(aux);
        }
        return listt;
    }
    private List<ClickAdds> listByCountry(Date desde, Date hasta) {
        List<Click> list = clickRepository.findAll();
        List<ClickAdds> listt = new ArrayList<ClickAdds>();

        HashMap<String, Integer> names = new HashMap<String, Integer>();
        int indice = 0;
        for (Click a : list) {
            if(desde !=null  && hasta!= null) {
                if (a.getDate().after(desde) && a.getDate().before(hasta)) {

                    if (names.containsKey(a.getCountry())) {
                        names.replace(a.getCountry(), names.get(a.getCountry()) + 1);
                    } else {
                        names.put(a.getCountry(), 1);
                    }
                }
            }else if (desde == null && hasta !=null){
                if(a.getDate().before(hasta)){
                    if (names.containsKey(a.getCountry())) {
                        names.replace(a.getCountry(), names.get(a.getCountry()) + 1);
                    } else {
                        names.put(a.getCountry(), 1);
                    }
                }
            }
            else if(desde !=null && hasta ==null){
                if(a.getDate().after(desde)){
                    if (names.containsKey(a.getCountry())) {
                        names.replace(a.getCountry(), names.get(a.getCountry()) + 1);
                    } else {
                        names.put(a.getCountry(), 1);
                    }
                }
            }else{return null;}
        }
        Set keys = names.keySet();
        Iterator iterator=keys.iterator();
        while(iterator.hasNext()){
            String key = (String)iterator.next();
            ClickAdds aux = new ClickAdds(key,names.get(key));
            listt.add(aux);
        }
        return listt;
    }
}
