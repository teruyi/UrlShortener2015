package urlshortener.bangladeshgreen.availableQueue;


import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Listener of the "availableQueue" queue.
 * It's the class called when a new message is ready in the queue.
 * When there is a new message in the queue, it creates a worker with the message and executes it.
 */
@Component
public class AvailableListener {

    @Autowired
    @Qualifier("availableExecutor")
    TaskExecutor executor;

    @Autowired
    AvailableWorker worker;

    @RabbitListener(queues="availableQueue")
    public void process(@Payload String URI) {
        //Cuando hay mensaje en la cola, se lanza worker a traves del pool
        worker.setParameter(URI); executor.execute(worker);
    }
}
