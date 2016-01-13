package urlshortener.bangladeshgreen.availableQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import urlshortener.bangladeshgreen.domain.URIAvailable;
import urlshortener.bangladeshgreen.repository.URIAvailableRepository;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Semaphore;

/**
 * Worker that is executed by the listener of the queue ("availableQueue").
 * It checks the URI set in the Worker parameter, and inserts the result in the DB (for caching).
 * The check consists in one GET request to the target URI.
 * If the URI has been checked before (before an hour ago), it doesn't make the request.
 */
@Component
public class AvailableWorker implements Runnable {


    private static final Logger logger = LoggerFactory.getLogger(AvailableWorker.class);

    @Autowired
    private URIAvailableRepository repository;

    private Semaphore lock = new Semaphore(1);
    private String workerParameter;
    public void setParameter(String param){
        try {
            // Sets a lock around the parameter (can be overwritten).
            lock.acquire();
            this.workerParameter = param;
        } catch (InterruptedException e) {
            logger.error("Available worker: Failing with locks");

        }
    }

    @Override
    public void run() {

        String parameter = workerParameter;
        lock.release();

        URIAvailable checked = checkURI(parameter);
        repository.save(checked);

        logger.info("\nAvailable Worker: \n------------------\n" + checked.toString());

    }

    /**
     * Checks if an URI is available (returns 2XX or 3XX code).
     * Allows redirections.
     * @param uriToCheck is the URI to check
     * @return update URI
     */
    protected URIAvailable checkURI(String uriToCheck){
        try {


            boolean isHttps =  false;
            boolean tooManyRedirects = false;
            URL url = new URL(uriToCheck);

            HttpURLConnection connection = null;
            if(uriToCheck.contains("https://")){
                isHttps = true;
                connection  = (HttpsURLConnection)url.openConnection();
            }
            else{
                connection  = (HttpURLConnection)url.openConnection();
            }




            connection.setRequestMethod("GET");

            // Sets default timeout to 3 seconds
            connection.setConnectTimeout(3000);

            //Used to bypass "only browsers" protection
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");


            // Connects to the URI to check.
            long connectionStartTime = System.currentTimeMillis();
            connection.connect();
            long connectionEndTime = System.currentTimeMillis();
            long connectionTotalTime = connectionEndTime-connectionStartTime;


            int doneRedirects = 0;
            Integer responseCode = new Integer(connection.getResponseCode());

            //Follow 5 redirects as maximum
            while(doneRedirects < 5 && (responseCode == 301 || responseCode == 302 || responseCode == 307)){

                //Follow redirect
                String location = connection.getHeaderField("Location");
                URL redirectURL = new URL(location);

                if(location.contains("http://") && isHttps){
                    //Change from https to http

                    connection = (HttpURLConnection)redirectURL.openConnection();
                    isHttps = false;
                }
                else if(location.contains("https://") && !isHttps){
                    //Change from http to https
                    connection = (HttpsURLConnection)redirectURL.openConnection();
                    isHttps = true;

                }
                //Try connection again


                connectionStartTime = System.currentTimeMillis();
                connection.connect();
                connectionEndTime = System.currentTimeMillis();
                connectionTotalTime = connectionEndTime-connectionStartTime;

                doneRedirects++;
                responseCode = new Integer(connection.getResponseCode());


            }

            if(doneRedirects>=5){
                //More than 5 redirects -> crazy
                tooManyRedirects = true;
            }

            //Check if uriAvailable is already on repository and updates stats if so
            URIAvailable uriAvailable = update(uriToCheck,connectionTotalTime,responseCode);

            if(uriAvailable != null){
                //Found in repository
                return uriAvailable;
            }else{
                //Not found in repository
                if ((responseCode.toString().charAt(0) == '2' || responseCode.toString().charAt(0) == '3')&&!tooManyRedirects) {
                    return new URIAvailable(workerParameter, true, System.currentTimeMillis(),1, false, true,"none");

                }else{
                    return new URIAvailable(workerParameter, false, System.currentTimeMillis(),1, false, true,"none");

                }
            }

        } catch (IOException e) {

            //Timeout has been reached

            URIAvailable uriAvailable = repository.findByTarget(uriToCheck);
            if(uriAvailable != null){

                uriAvailable.getDelays().add(new Long(3000)); //Total delays
                uriAvailable.getService().add(1);
                uriAvailable.setTimes(uriAvailable.getTimes()+1);
                uriAvailable.setAvailable(false);
                // if not available
                // time down
                uriAvailable.setNotAvailable(uriAvailable.getNotAvailable()+1);
                return uriAvailable;
            }else{
                return new URIAvailable(workerParameter, false, System.currentTimeMillis(),1, false, true,"none");

            }

        }
    }

    /**
     * Checks if URI is already on repository.
     * If it is, it update its stats.
     * Else, returns null.
     */
    protected URIAvailable update(String URI, long delay, Integer code){
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
