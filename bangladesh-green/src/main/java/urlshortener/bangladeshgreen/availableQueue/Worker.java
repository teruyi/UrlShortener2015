package urlshortener.bangladeshgreen.availableQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import urlshortener.bangladeshgreen.domain.URIAvailable;
import urlshortener.bangladeshgreen.repository.URIAvailableRepository;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.Semaphore;

/**
 * Worker that is executed by the listener of the queue ("availableQueue").
 * It checks the URI set in the Worker parameter, and inserts the result in the DB (for caching).
 * The check consists in one GET request to the target URI.
 * If the URI has been checked before (before an hour ago), it doesn't make the request.
 */
@Component
public class Worker implements Runnable {

    @Autowired
    private URIAvailableRepository repository;

    private Semaphore lock = new Semaphore(1);
    private String param;
    public void setParameter(String param){
        try {
            // Sets a lock around the parameter (can be overwritten).
            lock.acquire();
            this.param = param;
        } catch (InterruptedException e) {
            System.out.println("Worker: failing with locks.");

        }
    }

    @Override
    public void run() {
        String parameter = param;
        lock.release();
        long id =  Thread.currentThread().getId();
        Date now = new Date();
        System.out.println("---------------------------------------------------------------------------------------");
        System.out.println();
        System.out.println("AVAILABLE URI CHECK QUEUE" );
        System.out.println(parameter);

        URIAvailable checked = checkURI(parameter);
        System.out.println(checked.toString());
        System.out.println("------------------------------------------------");
        repository.save(checked);

    }

    /**
     * Checks if an URI is available (returns 2XX or 3XX code).
     * Allows redirections.
     * @param URI is the URI to check
     * @return actualize URI
     */
    protected URIAvailable checkURI(String URI){
        try {

            URL url = new URL(URI);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            // Sets default timeout to 3 seconds
            connection.setConnectTimeout(3000);
            // Connects to the URI to check.
            long t1 = System.currentTimeMillis();
            connection.connect();
            long t2 = System.currentTimeMillis();
            long t3 = t2-t1;

            Integer code = new Integer(connection.getResponseCode());
            URIAvailable uri = actualize(URI,t3,code);
            if(uri != null){
                return uri;
            }else{
                if (code.toString().charAt(0) == '2' || code.toString().charAt(0) == '3') {
                    URIAvailable newURIAvailable = new URIAvailable(param, true, System.currentTimeMillis(), true, false);
                    return newURIAvailable;
                }else{
                    URIAvailable newURIAvailable = new URIAvailable(param, false, System.currentTimeMillis(), true, false);
                    return newURIAvailable;
                }
            }

        } catch (IOException e) {

            URIAvailable uri = repository.findByTarget(URI);
            if(uri != null){
                uri.getDelays().add(new Long(3000));
                uri.getService().add(1);
                uri.setTimes(uri.getTimes()+1);
                uri.setAvailable(false);
                // if not available
                // time down
                uri.setNotAvailable(uri.getNotAvailable()+1);
                return uri;
            }else{
                return new URIAvailable(param, false, System.currentTimeMillis(), true, false);

            }

        }
    }

    protected URIAvailable actualize(String URI, long delay,Integer code){
        URIAvailable uri = repository.findByTarget(URI);
        if(uri != null) {
            // Add time-out
            uri.getDelays().add(delay);
            // Add count
            uri.setTimes(uri.getTimes() + 1);
            // if is available
            if (code.toString().charAt(0) == '2' || code.toString().charAt(0) == '3') {
                uri.setAvailable(true);
                // Time down
                uri.setNotAvailable(0);
                //Service time
                uri.getService().add(0);
                // if is disabled.
                if (!uri.isEnable()) {
                    uri.setChange(true);
                }
            } else {
                uri.setAvailable(false);
                // if not available
                // time down
                uri.setNotAvailable(uri.getNotAvailable() + 1);
                //Service time
                uri.getService().add(1);
            }
            return uri;
        }else{
            return null;
        }
    }
}
