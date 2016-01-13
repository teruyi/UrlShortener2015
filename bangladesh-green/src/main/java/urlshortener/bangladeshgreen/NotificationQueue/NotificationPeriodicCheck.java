package urlshortener.bangladeshgreen.NotificationQueue;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import urlshortener.bangladeshgreen.domain.*;
import urlshortener.bangladeshgreen.repository.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class for periodic check of the shorten URIs
 * It's scheduled and when it runs, obtains all the outdated links from the DB, checking all again.
 * It checks the URIs by inserting again the URIs in the queue.
 */
public class NotificationPeriodicCheck {
	// Interval that sets when a URI has to be checked again (1/4 h)

	@Autowired
	private RabbitTemplate rabbitTemplate;
	@Autowired
	private NotifyDisableRepository notifyDisableRepository;
	@Autowired
	private URIAvailableRepository availableRepository;
	@Autowired
	private ShortURLRepository shortURLRepository;
	@Autowired
	private NotifyRepository notifyRepository;
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private URIDisabledRepository disabledRepository;
	// One hour of delay (for checking "all" URIs)
	@Scheduled(fixedDelay = 180000L)
	public void send() {
		// All users
		List<String> users = new ArrayList<String>();
		List<User> us = userRepository.findAll();
		List<URIAvailable> changes = availableRepository.findByChange(true);
		List<ShortURL> urls = new ArrayList<ShortURL>();
		List<URIDisabled> disableds = new ArrayList<URIDisabled>();
		// For all AvailableURI wich have a change, save all URLShort with the same target
		List<String> usersSend = new ArrayList<String>();
		List<Notify> not = notifyRepository.findAll();
		List<NotifyDisable> notd = notifyDisableRepository.findAll();
		if (changes.size() >0) {


			for (User user: us) {
				urls = shortURLRepository.findByCreator(user.getUsername());

				disableds = disabledRepository.findByCreator(user.getUsername());
				for (URIAvailable x : changes) {
					for(ShortURL a: urls) {
						if (a.getTarget().compareTo(x.getTarget()) == 0) {

							Notify ab = new Notify(x.getTarget(), user.getUsername());

							notifyRepository.save(ab);
						}
					}
				}
				for (URIAvailable z : changes) {
					for(URIDisabled n: disableds){
						if(n.getTarget().compareTo(z.getTarget())==0){
							NotifyDisable ab = new NotifyDisable(n.getHash(), z.getTarget());
							NotifyDisable abc = notifyDisableRepository.findByHash(n.getHash());
							if(abc == null){

								notifyDisableRepository.save(ab);
							}

						}
					}
				}
				this.rabbitTemplate.convertAndSend("notificationQueue", user.getUsername());

			}
		}
	}

}