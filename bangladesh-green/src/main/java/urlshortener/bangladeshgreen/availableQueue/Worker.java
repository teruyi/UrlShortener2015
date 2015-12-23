package urlshortener.bangladeshgreen.availableQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import urlshortener.bangladeshgreen.domain.URIAvailable;
import urlshortener.bangladeshgreen.repository.URIAvailableRepository;

import java.io.IOException;
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
public class Worker implements Runnable {

    // Interval that sets when a URI has to be checked again (1h)
    // private final long interval = 3600*1000;
    private final long interval = 1000;


    @Autowired
    private URIAvailableRepository repository;

    private String param;
    public synchronized void setParameter(String param){
        this.param = param;
    }

    @Override
    public synchronized void run() {
        long id =  Thread.currentThread().getId();
        System.out.println("[URIAvailable] Worker - " + param + " - ID: " + id);
        URIAvailable old = repository.findByTarget(param);
        Date now = new Date();
        if(old == null) {
            System.out.println("[URIAvailable] Worker - " + param + " - ID: " + id + " - "
                   + "URI not checked, checking now: " + param);
            boolean check = checkURI(param);
            URIAvailable checked = new URIAvailable(param, check, now.getTime());
            repository.save(checked);
        } else {
            if(now.getTime()-old.getDate()>interval){
                System.out.println("[URIAvailable] Worker - " + param + " - ID: " + id + " - "
                       + "URI checked long time ago, doing check again: " + param);
                boolean check = checkURI(param);
                URIAvailable checked = new URIAvailable(param, check, now.getTime());
                repository.save(checked);
            }
        }
    }

    /**
     * Checks if an URI is available (returns 2XX or 3XX code).
     * Allows redirections.
     * @param URI is the URI to check
     * @return boolean True if available, false in other case.
     */
    protected boolean checkURI(String URI){
        try {
            URL url = new URL(URI);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            // Sets default timeout to 3 seconds
            connection.setConnectTimeout(3000);
            // Connects to the URI to check.
            connection.connect();
            Integer code = new Integer(connection.getResponseCode());
            // If it returns 2XX or 3XX code, the check it's successful
            if( code.toString().charAt(0) == '2' || code.toString().charAt(0) == '3'){
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
    }
}
