package urlshortener.bangladeshgreen.colaEjemplo;


import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Listener de la cola de ejemplo.
 * Es la clase que se llama cuando hay algo nuevo en la cola.
 * En este caso, lanza un thread worker que lo unico que hace es imprimir un mensaje por pantalla,
 * pero que podría enviar un mail, comprobar URL, etc.
 */
@Component
public class ListenerEjemplo {

    @Autowired
    @Qualifier("taskExecutorEjemplo")
    TaskExecutor executor;

    @Autowired
    WorkerEjemplo worker;

    @RabbitListener(queues="colaEjemplo")
    public void process(@Payload String foo) {
        //Cuando hay mensaje en la cola, se lanza worker a traves del pool
        worker.setParameter(foo); executor.execute(worker);
    }



}
