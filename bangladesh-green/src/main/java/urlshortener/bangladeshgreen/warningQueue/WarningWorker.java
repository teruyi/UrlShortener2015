package urlshortener.bangladeshgreen.warningQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import urlshortener.bangladeshgreen.availableQueue.AvailableWorker;
import urlshortener.bangladeshgreen.domain.ShortURL;
import urlshortener.bangladeshgreen.domain.URIAvailable;
import urlshortener.bangladeshgreen.domain.URIDisabled;
import urlshortener.bangladeshgreen.domain.User;
import urlshortener.bangladeshgreen.repository.ShortURLRepository;
import urlshortener.bangladeshgreen.repository.URIAvailableRepository;
import urlshortener.bangladeshgreen.repository.URIDisabledRepository;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Worker that is executed by the listener of the queue ("warningQueue").
 * It checks the URI set in the Worker parameter, and inserts the result in the DB (for caching).
 * The check consists in one GET request to the target URI.
 * If the URI has been checked before (before an hour ago), it doesn't make the request.
 */
@Component
public class WarningWorker implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(AvailableWorker.class);
    @Autowired
    private URIAvailableRepository repository;

    @Autowired
    private URIDisabledRepository repositoryURIDisabled;

    @Autowired
    private ShortURLRepository repositorySHORT;

    private Semaphore lock = new Semaphore(1);
    @Value("${db.periodCheck}")
    private int PERIOD_CHECK;
    @Value("${db.limitNotAvailable}")
    private int LIMIT_NOT_AVAILABLE;
    @Value("${db.limitTimeServiceAverage}")
    private int LIMIT_TIME_SERVICE_AVERAGE;
    @Value("${db.limitDelay}")
    private int LIMIT_DELAY;

    private String param;

    public void setParameter(String param) {
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
        long id = Thread.currentThread().getId();
        Date now = new Date();


        checkURI(parameter);
    }

    /**
     * Checks if an URI is warning (returns 2XX or 3XX code).
     * Allows redirections.
     *
     * @param URI is the URI to check
     * @return update URI
     */
    protected void checkURI(String URI) {

        URIAvailable uri = repository.findByTarget(URI);

        // Delay time average
        double delayAverage = delay(uri.getDelays());
        // Service Time average
        double serviceAverage = serviceAverage(uri.getService());

        // First check if improve
        String cause = "";
        if (uri.getNotAvailable() > LIMIT_NOT_AVAILABLE) {
            cause = "down";
        }
        if (delayAverage > LIMIT_DELAY) {
            cause = "delay";
        }
        if (serviceAverage < LIMIT_TIME_SERVICE_AVERAGE) {
            cause = "service";
        }

        checkProblems(uri, delayAverage, serviceAverage, cause);


    }

    protected void checkProblems(URIAvailable uri, double delayAverage, double serviceAverage, String cause) {

        if (uri.getNotAvailable() > LIMIT_NOT_AVAILABLE
                || delayAverage > LIMIT_DELAY
                || (serviceAverage *100) < LIMIT_TIME_SERVICE_AVERAGE) {
            // if times are bad -> change state to notify and reset times

            uri.setChange(true);
            uri.getService().clear();
            uri.setTimes(0);
            uri.setNotAvailable(0);
            uri.getDelays().clear();
            uri.setState(uri.getState() + 1); // state notify
            uri.setChange(true);
            uri.setProblem(cause);
            repository.save(uri);
            logger.info("\nWarning Worker: \n----------------\n" + uri.toString());


        } else {
            // if times are good
            uri.getService().clear();
            uri.getService().clear();
            uri.setTimes(0);
            uri.setAvailable(true);
            uri.setNotAvailable(0);
            uri.getDelays().clear();

            if(uri.getState() == 3){
                uri.setState(1);
                uri.setChange(true);


            }
            repository.save(uri);
            logger.info("\nWarning Worker: \n----------------\n" + uri.toString());
        }
    }

    private double serviceAverage(List<Integer> service) {
        double average = 0.0;
        for (Integer a : service) {
            average = average + a;
        }
        average = average / service.size();
        average = 1.0 - average;
        return average;
    }

    private double delay(List<Long> delays) {
        double average = 0.0;
        for (Long a : delays) {
            average = average + a;
        }
        average = average / delays.size();
        return average;
    }
}
