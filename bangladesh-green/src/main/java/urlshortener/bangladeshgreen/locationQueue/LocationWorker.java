package urlshortener.bangladeshgreen.locationQueue;

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
import java.util.Scanner;

/**
 * Created by teruyi on 27/12/15.
 */
@Component
public class LocationWorker implements Runnable{

    @Autowired
    protected ClickRepository clickRepository;

    private static final Logger logger = LoggerFactory.getLogger(LocationWorker.class);
    private String p1;
    private String hash;
    private String ip;

    public void setParameter(String p1){
        this.p1 = p1;
    }


    @Override
    public void run() {
        setparameters();

        Date now = new Date();
        Click click = new Click(hash,now,ip);

        long id =  Thread.currentThread().getId();
        logger.info("[LocationInfo] Worker - ID: " + id);

        Location location = getLocation(ip);
        if (location != null){
            click.setCity(location.getCity());
            click.setCountry(location.getCountry());
            click.setCountryCode(location.getCountryCode());
            click.setRegion(location.getRegion());
            click.setRegionName(location.getRegionName());
            logger.info("[LocationInfo] Worker - ID: " + id + " Gets location IP " + ip);
        }else{
            logger.info("[LocationInfo] Worker - ID: " + id + " Fail getting location IP " + ip);
        }

        Click cl = clickRepository.save(click);
        logger.info(cl!=null? "[LocationInfo] Worker - ID: " + id +
                "["+cl.getHash()+"] saved with date ["+cl.getDate()+"]"+
                "[LocationInfo] Worker - ID: " + id :"["+cl.getHash()+"] was not saved");
    }

    protected void setparameters (){
        Scanner s = new Scanner(p1);
        s.useDelimiter(",");
        ip = s.next();
        hash = s.next();

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
                return location;
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }


}
