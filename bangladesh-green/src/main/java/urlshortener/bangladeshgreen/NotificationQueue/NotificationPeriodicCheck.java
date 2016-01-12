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
	@Scheduled(fixedDelay = 520000L)
	public void send() {
		// All users
		List<String> users = new ArrayList<String>();
		List<URIAvailable> changes= availableRepository.findByChange(true);
		List<ShortURL> urls = new ArrayList<ShortURL>();
		List<URIDisabled> disableds = new ArrayList<URIDisabled>();
		// For all AvailableURI wich have a change, save all URLShort with the same target
		List<String> usersSend = new ArrayList<String>();
		for(URIAvailable a:changes){
			if(a.getState() < 4 ) {
				List<ShortURL> b = shortURLRepository.findByTarget(a.getTarget());
				for (ShortURL c : b) {
					if (!users.contains(c.getCreator())) {
						users.add(c.getCreator());

					}
				}
			}else{
				List<URIDisabled> b = disabledRepository.findByTarget(a.getTarget());
				for (URIDisabled c : b) {
					if (!users.contains(c.getCreator())) {
						users.add(c.getCreator());

					}
				}
			}
		}


		for(String user: users){
			urls = shortURLRepository.findByCreator(user);
			disableds = disabledRepository.findByCreator(user);
			for(ShortURL x: urls){
				if(urls.contains(x)){
					usersSend.add(user);
					Notify ab = new Notify(x.getTarget(),user);

					notifyRepository.save(ab);
					this.rabbitTemplate.convertAndSend("notificationQueue",user);
				}
			}
			for(URIDisabled x: disableds){
				if(!usersSend.contains(user)){
					usersSend.add(user);
					Notify ab = new Notify(x.getTarget(),user);

					notifyRepository.save(ab);
					this.rabbitTemplate.convertAndSend("notificationQueue",user);
				}
			}

		}

	}

}