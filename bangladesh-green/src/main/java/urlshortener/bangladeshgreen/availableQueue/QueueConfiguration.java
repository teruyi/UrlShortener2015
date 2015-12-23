package urlshortener.bangladeshgreen.availableQueue;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Configures beans for managing the "availableQueue" queue.
 * It registers the queue, listener, threads pool and periodic check (thread).
 * Author: bangladesh-green
 */
@Configuration
@EnableScheduling
public class QueueConfiguration {

    @Bean
    // Register the desired queue
    public Queue availableQueue() {
        return new Queue("availableQueue");
    }

    @Bean
    // Register the listener that takes messages from queue
    public Listener listener(){
        return new Listener();
    }

    @Bean
    // Periodic check for available URIs (in one thread).
    public PeriodicCheck periodicCheck() {
        return new PeriodicCheck();
    }

    @Bean
    // Thread pool of the queue. It takes threads (workers) from this pool to do certain tasks.
    public TaskExecutor availableExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        // If the queue is full (MAX_INT_VALUE), then it can create this number of threads.
        taskExecutor.setMaxPoolSize(10);
        // It can be X process concurrently.
        taskExecutor.setCorePoolSize(10);
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }

}
