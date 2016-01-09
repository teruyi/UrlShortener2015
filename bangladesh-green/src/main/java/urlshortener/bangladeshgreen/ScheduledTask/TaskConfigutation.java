package urlshortener.bangladeshgreen.ScheduledTask;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by teruyi on 9/01/16.
 * configure PeriodicCpuRam
 */
@Configuration
@EnableScheduling
public class TaskConfigutation {

    @Bean
    // Periodic check cpu and ram usage (in one thread).
    public PeriodicCpuRam PeriodicCpuRam() {
        return new PeriodicCpuRam();
    }
}
