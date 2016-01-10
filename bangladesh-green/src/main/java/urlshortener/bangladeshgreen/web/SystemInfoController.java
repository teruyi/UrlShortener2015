package urlshortener.bangladeshgreen.web;

import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import urlshortener.bangladeshgreen.domain.Click;
import urlshortener.bangladeshgreen.domain.Usage;
import urlshortener.bangladeshgreen.domain.UsageCpu;
import urlshortener.bangladeshgreen.domain.UsageRam;
import urlshortener.bangladeshgreen.domain.messages.ErrorResponse;
import urlshortener.bangladeshgreen.domain.messages.SuccessResponse;
import urlshortener.bangladeshgreen.repository.CPURepository;
import urlshortener.bangladeshgreen.repository.ClickRepository;
import urlshortener.bangladeshgreen.repository.RamRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by teruyi.
 */

/**
 * Controller used for show URL info.
 * When a user wants to get info about an URL, the user make a get request with the URL followed by '+' and
 * the controller return a JSON or HTML redirect with the Information.
 */
@Controller
public class SystemInfoController {

    private static final Logger logger = LoggerFactory.getLogger(SystemInfoController.class);

    @Autowired
    protected ClickRepository clickRepository;

    @Autowired
    protected CPURepository cpuRepository;

    @Autowired
    protected RamRepository ramRepository;




    /**
     * Returns the following info about the system, depending on the parameters.
     * RAM: 30 seconds series for a day, or average on a day.
     * CPU: 30 seconds series for a day, or average on a day.
     * CLICKS: 1 hour series for a day, total for a day or total for all time.
     *
     * Parameters:
     * type: Can be cpu, ram, clicks (total clicks a day or all time) or clicksadds (series of clicks for a day)
     * day: Day. Mandatory with all types except 'clicks'. If not present, clicks for all time will be retrieved.
     * Series: Can be 'series' or 'average'. Only applicated to type=ram or type=cpu.
    */
    @RequestMapping(value = "/systeminfo", method = RequestMethod.GET , produces ="application/json")
    public Object locationJson(@RequestParam(value="type", required=true) String type,
                               @RequestParam(value="day", required=false) Date day,
                               @RequestParam(value="series", required=false) String series,
                               HttpServletResponse response, HttpServletRequest request) {


        //Get authentication information
        final Claims claims = (Claims) request.getAttribute("claims");
        String loggedRoles = (String) claims.get("roles");


        //If global and not admin -> Forbidden
        if(!loggedRoles.equalsIgnoreCase("admin")){
            //Not authorized
            ErrorResponse errorResponse = new ErrorResponse("Permission denied");
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }


        List <Usage> list = null;

        //CPU or RAM average in day
        if ((type.compareTo("cpu") == 0 || type.compareTo("ram") == 0) && series!=null && series.compareTo("average")==0 && day!=null){
            double average = listCPURamAverage(day,type);
            logger.info("(/systeminfo) - ("+ type +") Ok request - average: " + average);
            SuccessResponse success = new SuccessResponse(average);
            response.setStatus(HttpStatus.OK.value());
            return new ResponseEntity<>(success, HttpStatus.OK);


        }
        //CPU or RAM series for a day. Interval: 30 seconds
        else if ((type.compareTo("cpu") == 0 || type.compareTo("ram") == 0)  && series!=null  && series.compareTo("series")==0 && day!=null){
            list = listCPURam(day,type);
            logger.info("(/systeminfo) - ("+ type +") Ok request - list size: " + list.size());
            SuccessResponse success = new SuccessResponse(list);
            response.setStatus(HttpStatus.OK.value());
            return new ResponseEntity<>(success, HttpStatus.OK);

        }
        //Clicks aggregation, by day or total
        else if (type.compareTo("clicks")==0){
            //If day is null, return total clicks. Else, return clicks at the given day.
            long total = listclicks(day);
            logger.info("(/systeminfo) - (clicks) Ok request - total: " + total);
            SuccessResponse success = new SuccessResponse(total);
            response.setStatus(HttpStatus.OK.value());
            return new ResponseEntity<>(success, HttpStatus.OK);

        }
        //Click list by hour for a given day
        else if (type.compareTo("clicksadds")==0 && day!=null){
            int hours [] = listclickshourAgregation(day);
            SuccessResponse success = new SuccessResponse(hours);
            response.setStatus(HttpStatus.OK.value());
            return new ResponseEntity<>(success, HttpStatus.OK);
        }
        else {
            //Bad request
            logger.info("(/systeminfo) Bad request");
            ErrorResponse error = new ErrorResponse("Bad request");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Returns average usage of CPU or RAM
     */
    private double listCPURamAverage(Date day, String type){
        List <Usage> list = listCPURam(day,type);
        double average = 0.0;
        for (Usage a : list) {
            average = average + a.getUsage();
        }
        return (average / list.size());
    }

    /**
     * Returns array with clicks by hours [00-23] at a given day
     */
    private int [] listclickshourAgregation(Date day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day);
        int hours[] = new int [24];

        for (int i = 0; i < 24; i++){
            long previous_time = calendar.getTimeInMillis();
            calendar.add(Calendar.HOUR, 1);
            long after_time = calendar.getTimeInMillis();
            List<Click> list = clickRepository.findByDateBetween(new Date(previous_time),new Date(after_time));
            hours[i] = list.size();
        }
        return hours;

    }


    /**
     * Returns total number of clicks one day
     */
    private long listclicks(Date day) {
        List<Click> list = clickRepository.findAll();
        long total = 0;
        for (Click a : list) {
            if (day == null){
                total++;
            }else{
                SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
                boolean sameDay = fmt.format(a.getDate()).equals(fmt.format(day));
                if(sameDay){
                    total++;
                }
            }

        }
        return  total;
    }

    /**
     * Returns series(time, usage) of cpu or ram at day
     */
    private List<Usage> listCPURam(Date day, String type) {
        ArrayList<Usage> listt;
        if (type.compareTo("cpu")==0){
            List<UsageCpu> list;
            list = cpuRepository.findAll();
            listt = new ArrayList<>();
            for (UsageCpu a : list) {
                Date dateUsage = new Date (a.getTime());
                SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
                boolean sameDay = fmt.format(dateUsage).equals(fmt.format(day));
                if(sameDay){
                    listt.add(a);
                }
            }
        } else {
            List<UsageRam> list;
            list = ramRepository.findAll();
            listt = new ArrayList<>();
            for (UsageRam a : list) {
                Date dateUsage = new Date (a.getTime());
                SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
                boolean sameDay = fmt.format(dateUsage).equals(fmt.format(day));
                if(sameDay){
                    listt.add(a);
                }
            }
        }
        return listt;
    }







}
