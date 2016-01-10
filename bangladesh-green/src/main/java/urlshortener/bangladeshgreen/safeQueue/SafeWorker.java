package urlshortener.bangladeshgreen.safeQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import urlshortener.bangladeshgreen.domain.URISafe;
import urlshortener.bangladeshgreen.repository.URISafeRepository;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.Semaphore;

/**
 * Worker that is executed by the listener of the queue ("safeQueue").
 * It checks the URI set in the Worker parameter, and inserts the result in the DB (for caching).
 * The check consists in one GET request to the target URI.
 * If the URI has been checked before (before an hour ago), it doesn't make the request.
 */
@Component
public class SafeWorker implements Runnable {


    private static final Logger logger = LoggerFactory.getLogger(SafeWorker.class);

    @Autowired
    private URISafeRepository repository;

    @Value("${token.safe_browsing_key}")
    private String GOOGLE_KEY;
    private Semaphore lock = new Semaphore(1);

    private String param;
    public void setParameter(String param){
        try {
            // Sets a lock around the parameter (can be overwritten).
            lock.acquire();
            this.param = param;
        } catch (InterruptedException e) {
            logger.error("Safe Worker: failing with locks");
        }
    }

    @Override
    public void run() {
        String parameter = param;
        lock.release();

        Date now = new Date();

        boolean check = checkSafeURI(parameter);
        URISafe checked = new URISafe(parameter, check, now.getTime());
        repository.save(checked);

        logger.info("Safe Worker: " + checked);
    }

    /**
     * Checks if an URI is safe
     * Allows redirections.
     * @param URI is the URI to check
     * @return boolean True if safe, false in other case.
     */
    protected boolean checkSafeURI(String URI){
        try{


            URL google = new
                    URL("https://sb-ssl.google.com/safebrowsing/api/lookup?client=api&key="+GOOGLE_KEY+"&appver=1.5.2&pver=3.1&url="+URI);
            HttpURLConnection connection = (HttpURLConnection)google.openConnection();
            connection.setRequestMethod("GET");

            // Sets default timeout to 3 seconds
            connection.setConnectTimeout(3000);
            // Connects to the URI to check.
            connection.connect();

            Integer code2 = new Integer(connection.getResponseCode());
            String respuesta = new String(connection.getResponseMessage());

            if (code2.toString().compareTo("204")== 0){
                return true;
            } else { return false;}
        }
        catch(IOException ex){
            ex.printStackTrace();
            return false;
        }
    }
}
