package urlshortener.bangladeshgreen.NotificationQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import urlshortener.bangladeshgreen.domain.ShortURL;
import urlshortener.bangladeshgreen.domain.URIAvailable;
import urlshortener.bangladeshgreen.domain.URIDisabled;
import urlshortener.bangladeshgreen.domain.User;
import urlshortener.bangladeshgreen.repository.ShortURLRepository;
import urlshortener.bangladeshgreen.repository.URIAvailableRepository;
import urlshortener.bangladeshgreen.repository.URIDisabledRepository;
import urlshortener.bangladeshgreen.repository.UserRepository;
import urlshortener.bangladeshgreen.secure.Email;

import java.net.URI;
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
public class NotificationWorker implements Runnable {

    @Autowired
    private URIDisabledRepository repositoryURIDisabled;

    @Autowired
    private ShortURLRepository repositorySHORT;

    @Autowired
    private URIAvailableRepository repositoryAvailable;

    @Autowired
    private UserRepository repositoryUser;

    @Autowired
    protected Email email;

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
        System.out.println("USERS NOTIFICATION QUEUE" );
        System.out.println(parameter);
        checkUser(parameter);

        System.out.println("---------------------------------------------------------------------------------------");

    }

    /**
     * Checks if an user has warnings.
     * @param user is the user to check
     */
    protected void checkUser(String user){

        List<URIDisabled> uri = repositoryURIDisabled.findByCreator(user);
        List<String> newEnabled = new ArrayList<String>();
        List<String> newDisabled = new ArrayList<String>();

        List<URIDisabled> enab = new ArrayList<URIDisabled>();
        List<URIDisabled> disa = new ArrayList<URIDisabled>();
            // Separe new disabled, new enabled
        for(URIDisabled a : uri){
            //Has changes
            if(a.isChange()){
                //new Enabled
                URIAvailable availables = repositoryAvailable.findByTarget(a.getTarget());
                if(availables.isEnable()) {
                    //delete from Disabled
                    repositoryURIDisabled.delete(a);
                    ShortURL enabled = new ShortURL(a.getHash(), a.getTarget(), a.getUri(),
                            a.getCreator(), a.getCreated(), a.getIp(), a.isPrivateURI(),
                            a.getPrivateToken(), a.getExpirationSeconds(), a.getAuthorizedUsers());
                    if(!newEnabled.contains(enabled.getTarget())){
                        newEnabled.add(enabled.getTarget());
                        enab.add(a);
                    }
                    a.setChange(false);
                    repositoryURIDisabled.save(a);
                    repositorySHORT.save(enabled);
                }else{
                    //new Disabled
                    if(!newDisabled.contains(a.getTarget())){
                        newDisabled.add(a.getTarget());
                        disa.add(a);
                    }
                    a.setChange(false);
                    repositoryURIDisabled.save(a);
                }
            }
        }
        if(newDisabled.size()>0 ||newEnabled.size()>0) {
            writeEmail(enab, disa, user);
        }

    }
    private void writeEmail(List<URIDisabled> newEnabled,List<URIDisabled> newDisabled, String destino){

        User userr = repositoryUser.findByUsername(destino);
        email.setDestination(userr.getEmail());
        email.sendNotification("Information Links","Information Links",newEnabled,newDisabled);
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
