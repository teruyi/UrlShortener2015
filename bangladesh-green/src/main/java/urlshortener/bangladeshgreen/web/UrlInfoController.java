package urlshortener.bangladeshgreen.web;

import io.jsonwebtoken.Claims;
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
import urlshortener.bangladeshgreen.domain.*;
import urlshortener.bangladeshgreen.domain.messages.ErrorResponse;
import urlshortener.bangladeshgreen.domain.messages.SuccessResponse;
import urlshortener.bangladeshgreen.repository.CPURepository;
import urlshortener.bangladeshgreen.repository.ClickRepository;
import urlshortener.bangladeshgreen.repository.RamRepository;
import urlshortener.bangladeshgreen.repository.ShortURLRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
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

    @Autowired
    protected CPURepository cpuRepository;

    @Autowired
    protected RamRepository ramRepository;


    @RequestMapping(value = "/{id:(?!link|index|info).*}+", method = RequestMethod.GET , produces ="text/html")
    public Object sendHtml(@PathVariable String id,
                             HttpServletResponse response, HttpServletRequest request,
                             Map<String, Object> model) {

        logger.info("Requested Link Info URL HTML with hash ");
        ShortURL l = shortURLRepository.findByHash(id);
        int count = clickRepository.findByHash(id).size();


        String userName = null; //Currently logged-in user username

        //Get authentication information
        final Claims claims = (Claims) request.getAttribute("claims");
        userName = claims.getSubject();
        String loggedRoles = (String) claims.get("roles");


        if (l != null) {

            if(userName.equalsIgnoreCase(l.getCreator()) ||  loggedRoles.equalsIgnoreCase("admin")){
                response.setStatus(HttpStatus.SEE_OTHER.value());
                model.put("url",l.getUri());
                model.put("target",l.getTarget());
                model.put("date",l.getCreated());
                model.put("count",count);
                return "info";
            }
            else{
                //Not authorized
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return "403";
            }


        } else {
            logger.info("Empty URL " + id);
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return "404";

        }
    }


    @RequestMapping(value = "/{id:(?!link|index).*}+", method = RequestMethod.GET , produces ="application/json")
    public Object sendJson(@PathVariable String id,
                                      HttpServletResponse response, HttpServletRequest request,
                                      Map<String, Object> model) {


        String userName = null; //Currently logged-in user username

        //Get authentication information
        final Claims claims = (Claims) request.getAttribute("claims");
        userName = claims.getSubject();
        String loggedRoles = (String) claims.get("roles");


        ShortURL l = shortURLRepository.findByHash(id);
        int count = clickRepository.findByHash(id).size();



        if (l != null) {

            if(userName.equalsIgnoreCase(l.getCreator()) ||  loggedRoles.equalsIgnoreCase("admin")) {
                InfoURL info = new InfoURL(l.getTarget(), l.getCreated().toString(), count);
                SuccessResponse success = new SuccessResponse(info);
                response.setStatus(HttpStatus.OK.value());
                return new ResponseEntity<>(success, HttpStatus.OK);
            }
            else{
                //Not authorized
                ErrorResponse errorResponse = new ErrorResponse("Permission denied");
                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
            }

        } else {
            logger.info("Empty URL " + id);
            response.setStatus(HttpStatus.NOT_FOUND.value());
            ErrorResponse error = new ErrorResponse("URL not found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }



    /**
     * Returns array of clickAdds. If id = null for all clicks else for id (hash) link.
     * Requires authentication.
     */
    @RequestMapping(value = "/info", method = RequestMethod.GET , produces ="application/json")
    public Object locationJson(@RequestParam(value="privateToken", required=false) String privateToken,
                               @RequestParam(value="type", required=true) String type,
                               @RequestParam(value="start", required=false) Date start,
                               @RequestParam(value="end", required=false) Date end,
                               @RequestParam(value="id", required=false) String id,
                               HttpServletResponse response, HttpServletRequest request,
                               Map<String, Object> model) {



        String userName = null; //Currently logged-in user username


        //Get authentication information
        final Claims claims = (Claims) request.getAttribute("claims");
        userName = claims.getSubject();
        String loggedRoles = (String) claims.get("roles");


        List <ClickAdds> list;


        ShortURL l = shortURLRepository.findByHash(id);

        //If not global (link), and link does not exist -> 404
        if(id!=null && l==null){
            //Does not exist
            logger.info("Empty URL " + id);
            response.setStatus(HttpStatus.NOT_FOUND.value());
            ErrorResponse error = new ErrorResponse("URL not found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        //If global and not admin -> Forbidden
        if(id ==null &&  !loggedRoles.equalsIgnoreCase("admin")){
            //Not authorized
            ErrorResponse errorResponse = new ErrorResponse("Permission denied");
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }

        //If not global and not creator nor admin -> Forbidden
        if(id !=null && !userName.equalsIgnoreCase(l.getCreator()) && !loggedRoles.equalsIgnoreCase("admin")){
            //Not authorized
            ErrorResponse errorResponse = new ErrorResponse("Permission denied");
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }


        if (type.compareTo("city")==0){
            list = listByCity(start, end, id);
            logger.info("(/info) - (city) Ok request - list size: " + list.size());
        } else if (type.compareTo("region")==0){

            list = listByRegion(start, end, id);
            logger.info("(/info) - (region) Ok request - list size: " + list.size());

        } else if (type.compareTo("country")==0){

            list = listByCountry(start, end, id);
            logger.info("(/info) - (country) Ok request - list size: " + list.size());

        } else {
            logger.info("(/info) Bad request");
            ErrorResponse error = new ErrorResponse("Bad request");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

        }


        SuccessResponse success = new SuccessResponse(list);
        response.setStatus(HttpStatus.OK.value());
        return new ResponseEntity<>(success, HttpStatus.OK);
    }



    private List<ClickAdds> listByRegion(Date desde, Date hasta, String id) {
        List<Click> list;

        if (id != null){
            list = clickRepository.findByHash(id);
        }
        else{
            list = clickRepository.findAll();
        }
        List<ClickAdds> listt = new ArrayList<ClickAdds>();

        HashMap<String, Integer> names = new HashMap<String, Integer>();
        int indice = 0;
        for (Click a : list) {
            if(desde !=null  && hasta!= null) {
                if ((a.getDate().after(desde)|| a.getDate().compareTo(desde) == 0) && (a.getDate().before(hasta)
                        || a.getDate().compareTo(hasta)==0)) {

                    if (names.containsKey(a.getRegionName())) {
                        names.replace(a.getRegionName(), names.get(a.getRegionName()) + 1);
                    } else {
                        names.put(a.getRegionName(), 1);
                    }
                }
            }else if (desde == null && hasta !=null){
                if(a.getDate().before(hasta) || a.getDate().compareTo(hasta) == 0){
                    if (names.containsKey(a.getRegionName())) {
                        names.replace(a.getRegionName(), names.get(a.getRegionName()) + 1);
                    } else {
                        names.put(a.getRegionName(), 1);
                    }
                }
            }
            else if(desde !=null && hasta ==null){
                if(a.getDate().after(desde) || a.getDate().compareTo(desde) == 0){
                    if (names.containsKey(a.getRegionName())) {
                        names.replace(a.getRegionName(), names.get(a.getRegionName()) + 1);
                    } else {
                        names.put(a.getRegionName(), 1);
                    }
                }
            }else if(desde == null && hasta == null){
                if (names.containsKey(a.getRegionName())) {
                    names.replace(a.getRegionName(), names.get(a.getRegionName()) + 1);
                } else {
                    names.put(a.getRegionName(), 1);
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
    private List<ClickAdds> listByCity(Date desde, Date hasta, String id) {
        List<Click> list;
        if (id != null){
            list = clickRepository.findByHash(id);
        }
        else{
            list = clickRepository.findAll();
        }
        List<ClickAdds> listt = new ArrayList<ClickAdds>();

        HashMap<String, Integer> names = new HashMap<String, Integer>();
        int indice = 0;
        for (Click a : list) {
            if(desde !=null  && hasta!= null) {
                if ((a.getDate().after(desde)|| a.getDate().compareTo(desde) == 0) && (a.getDate().before(hasta)
                        || a.getDate().compareTo(hasta)==0)) {
                    if (names.containsKey(a.getCity())) {
                        names.replace(a.getCity(), names.get(a.getCity()) + 1);
                    } else {
                        names.put(a.getCity(), 1);
                    }
                }
            }else if (desde == null && hasta !=null){
                if(a.getDate().before(hasta) || a.getDate().compareTo(hasta) == 0){
                    if (names.containsKey(a.getCity())) {
                        names.replace(a.getCity(), names.get(a.getCity()) + 1);
                    } else {
                        names.put(a.getCity(), 1);
                    }
                }
            }
            else if(desde !=null && hasta ==null){
                if(a.getDate().after(desde)){
                    if(a.getDate().after(desde) || a.getDate().compareTo(desde) == 0){
                        names.replace(a.getCity(), names.get(a.getCity()) + 1);
                    } else {
                        names.put(a.getCity(), 1);
                    }
                }
            }else if(desde == null && hasta == null){
                if(names.containsKey(a.getCity())){
                    names.replace(a.getCity(), names.get(a.getCity()) + 1);
                } else {
                    names.put(a.getCity(), 1);
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
    private List<ClickAdds> listByCountry(Date desde, Date hasta, String id) {
        List<Click> list;
        if (id != null){
            list = clickRepository.findByHash(id);
        }
        else{
            list = clickRepository.findAll();
        }
        List<ClickAdds> listt = new ArrayList<ClickAdds>();

        HashMap<String, Integer> names = new HashMap<String, Integer>();
        int indice = 0;
        for (Click a : list) {
            if(desde !=null  && hasta!= null) {
                if ((a.getDate().after(desde)|| a.getDate().compareTo(desde) == 0) && (a.getDate().before(hasta)
                        || a.getDate().compareTo(hasta)==0)) {
                    if (names.containsKey(a.getCountry())) {
                        names.replace(a.getCountry(), names.get(a.getCountry()) + 1);
                    } else {
                        names.put(a.getCountry(), 1);
                    }
                }
            }else if (desde == null && hasta !=null){
                if(a.getDate().before(hasta) || a.getDate().compareTo(hasta) == 0){
                    if (names.containsKey(a.getCountry())) {
                        names.replace(a.getCountry(), names.get(a.getCountry()) + 1);
                    } else {
                        names.put(a.getCountry(), 1);
                    }
                }
            }
            else if(desde !=null && hasta ==null){
                if(a.getDate().after(desde) || a.getDate().compareTo(desde) == 0){
                    if (names.containsKey(a.getCountry())) {
                        names.replace(a.getCountry(), names.get(a.getCountry()) + 1);
                    } else {
                        names.put(a.getCountry(), 1);
                    }
                }
            } else if(desde == null && hasta == null){
                if (names.containsKey(a.getCountry())) {
                    names.replace(a.getCountry(), names.get(a.getCountry()) + 1);
                } else {
                    names.put(a.getCountry(), 1);
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
}
