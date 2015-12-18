package urlshortener.bangladeshgreen.colaEjemplo;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Created by ismaro3 on 18/12/15.
 */
@Configuration
@EnableScheduling
/**
 * Configura los beans para manejar la colaEjemplo:
 *  * Define la cola, el escuchador, el pool de threads y el proceso periodico.
 *  Si queremos ser el que envia, solo necesitamos definir la cola.
 *  Si somos el que recibe, la cola, el escuchador, el pool de workers.
 *  El proceso periodico podria ir en cualquier parte.
 */
public class QueueConfigurationEjemplo {

    @Bean
    //Registramos la cola de ejemplo
    public Queue colaEjemplo() {
        return new Queue("colaEjemplo");
    }

    @Bean
    //Registramos quien escuchara dicha cola
    public ListenerEjemplo listenerEjemplo(){
        return new ListenerEjemplo();
    }

    @Bean
    //Proceso periodico que mete en cola de ejemplo
    public ProcesoPeriodicoEjemplo procesoPeriodicoEjemplo() {
        return new ProcesoPeriodicoEjemplo();
    }

    @Bean
    //El pool de threads de la colaEjemplo
    //Cada cola debe tener su pool de threads
    public TaskExecutor taskExecutorEjemplo() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(10); //Si la cola se llena (MAX_INT_VALUE), entonces podremos crear hasta los threads indicados
        taskExecutor.setCorePoolSize(10); //Podemos tener X concurrentemente
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }

}
