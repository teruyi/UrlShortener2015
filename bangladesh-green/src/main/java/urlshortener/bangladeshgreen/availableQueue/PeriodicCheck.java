package urlshortener.bangladeshgreen.availableQueue;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import urlshortener.bangladeshgreen.domain.URIAvailable;
import urlshortener.bangladeshgreen.repository.URIAvailableRepository;

import java.util.List;

/**
 * Class for periodic check of the shorten URIs
 * It's scheduled and when it runs, obtains all the outdated links from the DB, checking all again.
 * It checks the URIs by inserting again the URIs in the queue.
 */
public class PeriodicCheck {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private URIAvailableRepository availableRepository;

	// One hour of delay (for checking "all" URIs)
	@Scheduled(fixedDelay = 10000L)
	public void send() {
		List<URIAvailable> list = availableRepository.list();
		for(URIAvailable uri : list) {
			this.rabbitTemplate.convertAndSend("availableQueue",uri.getTarget());
		}
	}

}