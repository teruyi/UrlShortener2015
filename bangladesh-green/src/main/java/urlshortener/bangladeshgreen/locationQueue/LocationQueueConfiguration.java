package urlshortener.bangladeshgreen.locationQueue;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Created by teruyi on 27/12/15.
 */
@Configuration
@EnableScheduling
public class LocationQueueConfiguration {

    @Bean
    // Register the desired queue
    public Queue locaQueue1() {
        return new Queue("locaQueue1");
    }

    @Bean
    // Register the listener that takes messages from queue
    public LocationListener listener(){
        return new LocationListener();
    }

    @Bean
    // Thread pool of the queue. It takes threads (workers) from this pool to do certain tasks.
    public TaskExecutor locaExecutor1() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        // If the queue is full (MAX_INT_VALUE), then it can create this number of threads.
        taskExecutor.setMaxPoolSize(10);
        // It can be X process concurrently.
        taskExecutor.setCorePoolSize(10);
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }


}
