package urlshortener.bangladeshgreen.NotificationQueue;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Configures beans for managing the "warningQueue" queue.
 * It registers the queue, listener, threads pool and periodic check (thread).
 * Author: bangladesh-green
 */
@Configuration
@EnableScheduling
public class NotificationQueueConfiguration {

    @Bean
    // Register the desired queue
    public Queue notificationQueue() {
        return new Queue("notificationQueue");
    }

    @Bean
    // Register the listener that takes messages from queue
    public NotificationListener listenerNotification(){
        return new NotificationListener();
    }

    @Bean
    // Periodic check for available URIs (in one thread).
    public NotificationPeriodicCheck periodicCheckNotification() {
        return new NotificationPeriodicCheck();
    }

    @Bean
    // Thread pool of the queue. It takes threads (workers) from this pool to do certain tasks.
    public TaskExecutor notificationExecutor() {
        ThreadPoolTaskExecutor taskExecutorNotification = new ThreadPoolTaskExecutor();
        // If the queue is full (MAX_INT_VALUE), then it can create this number of threads.
        taskExecutorNotification.setMaxPoolSize(1);
        // It can be X process concurrently.
        taskExecutorNotification.setCorePoolSize(1);
        taskExecutorNotification.afterPropertiesSet();
        return taskExecutorNotification;
    }

}
