package urlshortener.bangladeshgreen.locationQuerue;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Configures beans for managing the "LocationQuerue" queue.
 * It registers the queue, listener, threads pool.
 * Author: bangladesh-green
 */
@Configuration
@EnableScheduling
public class LocationQuerueConfiguration {

    @Bean
    // Register the desired queue
    public Queue locationQueue() { return new Queue("locationQueue");
    }

    @Bean
    // Register the listener that takes messages from queue
    public LocationListener listener(){
        return new LocationListener();
    }



    @Bean
    // Thread pool of the queue. It takes threads (workers) from this pool to do certain tasks.
    public TaskExecutor locationExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        // If the queue is full (MAX_INT_VALUE), then it can create this number of threads.
        taskExecutor.setMaxPoolSize(5);
        // It can be X process concurrently.
        taskExecutor.setCorePoolSize(5);
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }

}
