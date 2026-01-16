package sokolov.spring.finalassignment.schedule;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    private final Integer eventSchedulingTime;

    public SchedulerConfig(
            @Value("${event.scheduling.time}") Integer eventSchedulingTime) {
        this.eventSchedulingTime = eventSchedulingTime;
    }

    public Integer getEventSchedulingTime(){
        return eventSchedulingTime;
    }
}
