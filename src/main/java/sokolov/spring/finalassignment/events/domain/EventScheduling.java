package sokolov.spring.finalassignment.events.domain;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class EventScheduling {

    private final EventService eventService;

    public EventScheduling(EventService eventService) {
        this.eventService = eventService;
    }


    @Scheduled(fixedRateString =  "${event.scheduling.time}" )
    public void eventsScheduling(){
        eventService.scheduleEvents();
    }
}
