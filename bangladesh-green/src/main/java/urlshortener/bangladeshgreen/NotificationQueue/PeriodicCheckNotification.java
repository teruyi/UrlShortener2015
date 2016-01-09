package urlshortener.bangladeshgreen.NotificationQueue;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import urlshortener.bangladeshgreen.domain.URIAvailable;
import urlshortener.bangladeshgreen.domain.URIDisabled;
import urlshortener.bangladeshgreen.domain.User;
import urlshortener.bangladeshgreen.repository.URIAvailableRepository;
import urlshortener.bangladeshgreen.repository.URIDisabledRepository;
import urlshortener.bangladeshgreen.repository.UserRepository;

import java.util.Date;
import java.util.List;

/**
 * Class for periodic check of the shorten URIs
 * It's scheduled and when it runs, obtains all the outdated links from the DB, checking all again.
 * It checks the URIs by inserting again the URIs in the queue.
 */
public class PeriodicCheckNotification {
	// Interval that sets when a URI has to be checked again (1/4 h)

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private URIDisabledRepository warningRepository;

	@Autowired
	private UserRepository userRepository;

	// One hour of delay (for checking "all" URIs)
	@Scheduled(fixedDelay = 15000L)
	public void send() {
		// All users
		List<User> users = userRepository.findAll();
		if(users != null)
		for(User user: users){
			List<URIDisabled> list = warningRepository.findByCreator(user.getUsername());
			if(list !=null ){
				if(list.size()>0){
						this.rabbitTemplate.convertAndSend("notificationQueue",user.getUsername());

				}
			}
		}
	}

}