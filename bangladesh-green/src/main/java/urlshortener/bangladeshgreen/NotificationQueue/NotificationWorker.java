package urlshortener.bangladeshgreen.NotificationQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import urlshortener.bangladeshgreen.availableQueue.AvailableWorker;
import urlshortener.bangladeshgreen.domain.*;
import urlshortener.bangladeshgreen.repository.*;
import urlshortener.bangladeshgreen.secure.Email;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Worker that is executed by the listener of the queue ("notificationQueue").
 * It report to users the information of their links by email and refresh the state of links.
 */
@Component
public class NotificationWorker implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(AvailableWorker.class);
    @Autowired
    private URIDisabledRepository repositoryURIDisabled;

    @Autowired
    private ShortURLRepository repositorySHORT;

    @Autowired
    private URIAvailableRepository repositoryAvailable;

    @Autowired
    private UserRepository repositoryUser;

    @Autowired
    private NotifyRepository notifyRepository;

    @Autowired
    protected Email email;

    @Autowired
    private NotifyDisableRepository notifyDisableRepository;

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
        logger.info("\nNotification Worker: \n----------------\n" + parameter);
        checkUser(parameter);

    }

    /**
     * Checks if an user has warnings.
     * @param user is the user to check
     */
    protected void checkUser(String user){
        List<URIAvailable> stateOne = new ArrayList<URIAvailable>(); // for  enable urls again
        List<URIAvailable> stateTwo = new ArrayList<URIAvailable>(); // for warning urls
        List<URIAvailable> stateThree = new ArrayList<URIAvailable>(); // for disable urls
        List<URIAvailable> stateFour = new ArrayList<URIAvailable>(); // for delete urls
        User user2 = repositoryUser.findByUsername(user);
        // All urls of user
        List<ShortURL> uris = repositorySHORT.findByCreator(user);
        try {


          // Four all urls of user, check changes
          for (ShortURL a : uris) {
              URIAvailable available = repositoryAvailable.findByTarget(a.getTarget());
              if (available.isChange()) {
                  logger.info("\nNotification Worker:" + available.toString());
                  //  Check what change has each url.
                  if (available.getState() == 2) {
                      if (!stateTwo.contains(available)) {
                          stateTwo.add(available);
                      }
                  } else if (available.getState() == 3) {
                      if (!stateThree.contains(available)) {
                          stateThree.add(available);
                      }

                  } else {
                      logger.info("\nNotification Worker:  ERROR," +
                              " bad state in URIAvailable: \n" + available.toString());
                  }
              }
          }

          List<URIDisabled> disab = repositoryURIDisabled.findByCreator(user);
          for (URIDisabled ax : disab) {
              URIAvailable available = repositoryAvailable.findByTarget(ax.getTarget());
              if (available != null){
                  if(available.getState() == 1) {
                      if (!stateOne.contains(available)) {
                          stateOne.add(available);
                      }
                  }
                  else if (available.getState() == 4) {
                      if (!stateFour.contains(available)) {
                          stateFour.add(available);
                      }

                  }
              }

          }
          if (stateOne.size() > 0 || stateTwo.size() > 0 || stateThree.size() > 0 || stateFour.size() > 0) {
              logger.info("" + stateFour.size());
              // First, send a e-mail.
              email.setDestination(user2.getEmail());
              email.sendNotification("Information Links",
                      "Information Links", stateOne, stateTwo, stateThree, stateFour);
              if (stateThree.size() > 0) {
                  checkState3(stateThree, user2);
              }
              if (stateFour.size() > 0) {
                  checkState4(stateFour, user2);
              }
              if (stateOne.size() > 0) {
                  checkState1(stateOne, user2);
              }
              for (URIAvailable a : stateTwo) {
                  Notify ab = notifyRepository.findById(a.getTarget() + user2.getUsername());
                  logger.info("\nNotification Worker: \n----------------\n" + ab);
                  notifyRepository.delete(ab.getId());
                  ab = notifyRepository.findById(ab.getId());
                  List<Notify> as = notifyRepository.findByTarget(a.getTarget());

                  if (as.size() == 0) {
                      a.setChange(false);
                      repositoryAvailable.save(a);
                  }

              }
          }
      }catch(Exception e){
          if (stateOne.size() > 0 || stateTwo.size() > 0 || stateThree.size() > 0 || stateFour.size() > 0) {
              logger.info("" + stateFour.size());
              // First, send a e-mail.
              email.setDestination(user2.getEmail());
              email.sendNotification("Information Links",
                      "Information Links", stateOne, stateTwo, stateThree, stateFour);
              if (stateThree.size() > 0) {
                  checkState3(stateThree, user2);
              }
              if (stateFour.size() > 0) {
                  checkState4(stateFour, user2);
              }
              if (stateOne.size() > 0) {
                  checkState1(stateOne, user2);
              }
              for (URIAvailable a : stateTwo) {
                  Notify ab = notifyRepository.findById(a.getTarget() + user2.getUsername());
                  logger.info("\nNotification Worker: \n----------------\n" + ab);
                  notifyRepository.delete(ab.getId());
                  ab = notifyRepository.findById(a.getTarget() + user2.getUsername());
                  List<Notify> as = notifyRepository.findByTarget(a.getTarget());

                  if (as.size() == 0) {
                      a.setChange(false);
                      repositoryAvailable.save(a);
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
    // Create DisableURI, delete ShortURL && URIAvailabe.enable = false
    private void checkState3(List<URIAvailable> stateThree, User user){

        /* Four each URLAvailable wich has a change in state 3,
         * search all ShortURL with the same target.
         */
        for(URIAvailable c : stateThree){
            List<ShortURL> uris = repositorySHORT.find(c.getTarget(),
                    user.getUsername());
            /*
             * For each ShortURL, create one URIDisable with same attributes and
             *  delete ShortURL and c.enable = false
             */
            for(ShortURL a: uris) {
                URIDisabled b = new URIDisabled(a.getHash(), a.getTarget(),
                        a.getUri(),a.getCreator(), a.getCreated(),
                        a.getIp(), a.isPrivateURI(),
                        a.getPrivateToken(), a.getExpirationSeconds(),
                        a.getAuthorizedUsers());
                repositoryURIDisabled.save(b);

                // Delete ShortURL
                repositorySHORT.delete(a);
                Notify ab = notifyRepository.find(a.getTarget(),a.getCreator());
                notifyRepository.delete(ab.getId());
                List<Notify> as = notifyRepository.findByTarget(c.getTarget());
                if(as.size() == 0) {

                    // Set enable = false
                    c.setEnable(false);
                    c.setChange(false);
                    repositoryAvailable.save(c);

                }
            }
        }
    }
    private void checkState4(List<URIAvailable> stateFour, User user){
        /* Delete DisableURI  && IF NOT EXIST MORE DisableURI
         * with uri.target -> delete uri
         */
        logger.info("\n ----------------CHECK 4-------------");
        for(URIAvailable a: stateFour){
            List<URIDisabled> disableds =
                    repositoryURIDisabled.find(a.getTarget(),user.getUsername());
            for(URIDisabled b: disableds){
                repositoryURIDisabled.delete(b.getHash());
                NotifyDisable ab = notifyDisableRepository.findByHash(b.getHash());
                notifyDisableRepository.delete(ab.getHash());

            }
            List<URIDisabled> disableds2 =
                    repositoryURIDisabled.findByTarget(a.getTarget());
            if(disableds2 !=null){
                if (disableds2.size() == 0){
                    repositoryAvailable.delete(a.getTarget());
                }
            }else{
                repositoryAvailable.delete(a.getTarget());
            }
        }
    }
    private void checkState1(List<URIAvailable> stateOne, User user){
        for(URIAvailable a:stateOne){
            List<URIDisabled> disableds =
                    repositoryURIDisabled.find(a.getTarget(),user.getUsername());
            for(URIDisabled b:disableds){
                ShortURL c = new ShortURL(b.getHash(),b.getTarget(),b.getUri(),
                        b.getCreator(),b.getCreated(),b.getIp(),b.isPrivateURI(),
                        b.getPrivateToken(),b.getExpirationSeconds(),
                        b.getAuthorizedUsers());
                repositorySHORT.save(c);
                repositoryURIDisabled.delete(b.getHash());
                notifyDisableRepository.delete(b.getHash());
                a.setChange(false);
                repositoryAvailable.save(a);
            }
        }

    }

}
