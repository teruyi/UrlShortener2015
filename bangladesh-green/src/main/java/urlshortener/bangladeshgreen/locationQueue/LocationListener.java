package urlshortener.bangladeshgreen.locationQueue;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Created by teruyi on 27/12/15.
 */
@Component
public class LocationListener {

    @Autowired
    @Qualifier("locaExecutor1")
    TaskExecutor executor;

    @Autowired
    LocationWorker worker;

    @RabbitListener(queues="locaQueue1")
    public void process(@Payload String IP) {
        //Cuando hay mensaje en la cola, se lanza worker a traves del pool
        worker.setParameter(IP); executor.execute(worker);
    }



}
