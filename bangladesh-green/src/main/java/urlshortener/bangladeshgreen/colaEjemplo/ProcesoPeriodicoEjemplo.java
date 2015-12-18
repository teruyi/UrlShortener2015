package urlshortener.bangladeshgreen.colaEjemplo;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Clase encargada de meter periodicamente mensajes en la cola de ejemplo
 */
public class ProcesoPeriodicoEjemplo {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Scheduled(fixedDelay = 1000L)
	public void send() {
		 this.rabbitTemplate.convertAndSend("colaEjemplo", "Mensaje periodico de cola de ejemplo");
	}

}