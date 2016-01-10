package urlshortener.bangladeshgreen.warningQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import urlshortener.bangladeshgreen.domain.ShortURL;
import urlshortener.bangladeshgreen.domain.URIAvailable;
import urlshortener.bangladeshgreen.domain.URIDisabled;
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
public class WorkerWarning implements Runnable {

    @Autowired
    private URIAvailableRepository repository;

    @Autowired
    private URIDisabledRepository repositoryURIDisabled;

    @Autowired
    private ShortURLRepository repositorySHORT;

    private int periodCheck= 2; //HOW MANY INTERVALS OF TIME ARE NECESARY TO ANALIZE THE TIMES
    private Semaphore lock = new Semaphore(1);

    private double limitDelay = 2000.0; // 2 seconds of response's delay
    private int limitNotAvailable = 2; // If receive 404 during 1/2 hour disable uri.
    private double limitTimeServiceAverage = 0.75; // The service must be active 75 % of time.
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
        System.out.println("[URIDisabled] Worker - " + parameter + " - ID: " + id + " - "
               + "Time to check URI: " + now.toString());

        checkURI(parameter);
    }

    /**
     * Checks if an URI is warning (returns 2XX or 3XX code).
     * Allows redirections.
     * @param URI is the URI to check
     * @return update URI
     */
    protected void checkURI(String URI){

            URIAvailable uri = repository.findByTarget(URI);

            // Delay time average
            double delayAverage = delay(uri.getDelays());

            // Service Time average
            double serviceAverage = serviceAverage(uri.getService());

        // First check if improve
        String cause="";
        if(uri.getNotAvailable() < limitNotAvailable){
            cause = "down";
        }
        if(delayAverage < limitDelay){
            cause = "delay";
        }
        if(serviceAverage < limitTimeServiceAverage){
           cause =  "service";
        }
        if(uri.isAvailable() && uri.getNotAvailable() < limitNotAvailable &&
                delayAverage < limitDelay && serviceAverage < limitTimeServiceAverage){
            //check times
            if(uri.getTimes() > periodCheck){
                List<URIDisabled> uris = repositoryURIDisabled.findByTarget(URI);
                // Its disabled
                if(uris!= null){
                    if(uris.size()>0){
                        //Enable it
                        for(URIDisabled a: uris){
                            a.setChange(true);
                            uri.setTimes(0);
                            uri.getService().clear();
                            uri.getDelays().clear();
                            uri.setNotAvailable(0);
                            uri.setEnable(true);

                            repository.save(uri);
                            System.out.println("HA MEJORADO");
                            System.out.println("hash"+ a.getHash() + "\n    cause:" + a.getCause() + "\n    target:" + a.getTarget());
                            repositoryURIDisabled.save(a);

                        }

                        URIAvailable aux = repository.findByTarget(URI);
                        aux.getDelays().clear();
                        aux.getService().clear();
                        aux.setTimes(0);
                        aux.setChange(false);
                        uri.setNotAvailable(0);
                        repository.save(aux);
                    }
                }
            }
            // if is disable, enabled.

        }
        // If not improve check times.
        else {
            if(uri.getTimes()>periodCheck){
                // Check if is disabled
                List<URIDisabled>a = repositoryURIDisabled.findByTarget(URI);
                if(a !=null ) {

                    if (a.size() > 0) {
                        if (uri.getTimes() > 5) {
                            uri.getDelays().clear();
                            uri.getService().clear();
                            uri.setTimes(0);
                            uri.setNotAvailable(limitNotAvailable);
                            uri.setChange(false);
                            uri.setEnable(false);
                            System.out.println("NO HA MEJORADO");
                            System.out.println(uri.toString());
                            repository.save(uri);
                        }
                    } else {
                        // Disable all URIs
                        List<ShortURL> urls = repositorySHORT.findByTarget(uri.getTarget());
                        if (urls != null) {

                            for (ShortURL ur : urls) {
                                repositorySHORT.delete(ur);
                                URIDisabled neww = new URIDisabled(ur.getHash(), ur.getTarget(),
                                        ur.getUri(),
                                        ur.getCreated(),
                                        ur.getCreator(),
                                        ur.getIp(),
                                        ur.getPrivateToken(),
                                        ur.isPrivateURI(),
                                        ur.getExpirationSeconds(),
                                        ur.getAuthorizedUsers(),
                                        true,
                                        cause);
                                uri.setEnable(false);
                                neww.setChange(true);
                                repository.save(uri);
                                System.out.println("DESHABILITADA");
                                System.out.println("hash" + neww.getHash() + "\n    cause:" + neww.getCause() + "\n    target:" + neww.getTarget());
                                repositoryURIDisabled.save(neww);
                            }

                        }
                    }

                }

                else {
                    if (uri.getTimes() > periodCheck) {
                        // Disable all URIs
                        List<ShortURL> urls = repositorySHORT.findByTarget(uri.getTarget());
                        if (urls != null) {
                            for (ShortURL ur : urls) {
                                repositorySHORT.delete(ur);
                                URIDisabled neww = new URIDisabled(ur.getHash(), ur.getTarget(),
                                        ur.getUri(),
                                        ur.getCreated(),
                                        ur.getCreator(),
                                        ur.getIp(),
                                        ur.getPrivateToken(),
                                        ur.isPrivateURI(),
                                        ur.getExpirationSeconds(),
                                        ur.getAuthorizedUsers(),
                                        true,
                                        cause);
                                uri.setEnable(false);
                                repository.save(uri);
                                System.out.println("DESHABILITADA");
                                System.out.println("hash" + neww.getHash() + "\n    cause:" + neww.getCause() + "\n    target:" + neww.getTarget());
                                repositoryURIDisabled.save(neww);
                            }

                        }
                    }
                }

            }
        }
    }

    private double serviceAverage(List<Integer> service) {
        double average = 0.0;
        for (Integer a: service){
            average = average + a;
        }
        average = average/service.size();
        average = 1.0 - average;
        return average;
    }

    private double delay(List<Long>delays){
        double average = 0.0;
        for(Long a: delays){
            average = average + a;
        }
        average = average/delays.size();
        return average;
    }
}
