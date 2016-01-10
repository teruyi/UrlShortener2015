package urlshortener.bangladeshgreen.safeQueue;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import urlshortener.bangladeshgreen.domain.URISafe;
import urlshortener.bangladeshgreen.repository.URISafeRepository;

import java.util.Date;
import java.util.List;

/**
 * Class for periodic check of the shorten URIs
 * It's scheduled and when it runs, obtains all the outdated links from the DB, checking all again.
 * It checks the URIs by inserting again the URIs in the queue.
 */
public class SafePeriodicCheck {
	// Interval that sets when a URI has to be checked again (1h)
	private final long interval = 3600*1000;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private URISafeRepository safeRepository;

	// One hour of delay (for checking "all" URIs)
	@Scheduled(fixedDelay = 3600000L)
	public void send() {
		Date now = new Date();
		now.setTime(now.getTime()-interval);
		List<URISafe> list = safeRepository.findByDateLessThan(now.getTime());
		for(URISafe uri : list) {
			this.rabbitTemplate.convertAndSend("safeQueue",uri.getTarget());
		}
	}

}