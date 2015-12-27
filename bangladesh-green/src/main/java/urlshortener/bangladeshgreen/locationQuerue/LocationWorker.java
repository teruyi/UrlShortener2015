package urlshortener.bangladeshgreen.locationQuerue;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import urlshortener.bangladeshgreen.domain.Click;
import urlshortener.bangladeshgreen.domain.Location;
import urlshortener.bangladeshgreen.repository.ClickRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * Worker that is executed by the listener of the queue ("availableQueue").
 * It checks the URI set in the Worker parameter, and inserts the result in the DB (for caching).
 * The check consists in one GET request to the target URI.
 * If the URI has been checked before (before an hour ago), it doesn't make the request.
 */
@Component
public class LocationWorker implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(LocationWorker.class);

    @Autowired
    protected ClickRepository clickRepository;

    //private Semaphore lock = new Semaphore(1);

    private String IP;
    private String hash;
    public void setParameter(String IP){
        try {
            // Sets a lock around the parameter (can be overwritten).
            //lock.acquire();
            this.IP = IP;
            //this.hash = IP.getHash();
        } catch (Exception e) {
            logger.info("Worker: failing with locks.");
        }
    }

    @Override
    public void run() {
        String parameter2 = IP;
        Click parameter = new Click();
        //lock.release();


        long id =  Thread.currentThread().getId();
        logger.info("[LocationInfo] Worker - " + parameter2 + " - ID: " + id);
        Date now = new Date();
        Location location = getLocation(parameter2);
        //Location location = getLocation(param.getIp(), id);
        if (location != null){
            parameter.setCity(location.getCity());
            parameter.setCountry(location.getCountry());
            parameter.setCountryCode(location.getCountryCode());
            parameter.setRegion(location.getRegion());
            parameter.setRegionName(location.getRegionName());
            logger.info("[LocationInfo] Worker - " + parameter.getIp() + " - ID: " + id + " Fail getting location");
        }else{
            logger.info("[LocationInfo] Worker - " + parameter.getIp() + " - ID: " + id + " gets location");
        }

        Click cl = clickRepository.save(parameter);
        logger.info(cl!=null? "[LocationInfo] Worker - " + parameter2 + " - ID: " + id +
                "["+cl.hashCode()+"] saved with date ["+cl.getDate()+"]"+
                "[LocationInfo] Worker - " + parameter2 + " - ID: " + id :"["+cl.getHash()+"] was not saved");


    }

    /**
     * Checks if an URI is available (returns 2XX or 3XX code).
     * Allows redirections.
     * @param IP is the URI to check
     * @return boolean True if available, false in other case.
     */
    protected Location getLocation(String IP){
        try {
            URL url = new URL("http://ip-api.com/json/" + IP);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            // Sets default timeout to 3 seconds
            connection.setConnectTimeout(3000);
            // Connects to the URI to check.
            connection.connect();
            Integer code = new Integer(connection.getResponseCode());
            // If it returns 2XX or 3XX code, the check it's successful
            if( code.toString().charAt(0) == '2' || code.toString().charAt(0) == '3'){
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                br.close();
                Location location = new Gson().fromJson(sb.toString(),Location.class);
                System.out.println(location);
                return location;
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }


}
