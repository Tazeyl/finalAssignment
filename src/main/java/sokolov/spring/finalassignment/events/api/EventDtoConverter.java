package sokolov.spring.finalassignment.events.api;

import org.springframework.stereotype.Component;
import sokolov.spring.finalassignment.events.domain.Event;
import sokolov.spring.finalassignment.events.domain.EventSearchFilter;
import sokolov.spring.finalassignment.events.domain.EventStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class EventDtoConverter {
    public EventDto to(Event event) {
        return new EventDto(
                event.id(),
                event.name(),
                event.ownerId(),
                event.maxPlaces(),
                event.occupiedPlaces(),
                event.date().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                event.cost(),
                event.duration(),
                event.locationId(),
                event.status().toString()
        );
    }

    public Event from(EventDto eventDto) {
        return new Event(
                eventDto.id(),
                eventDto.name(),
                eventDto.ownerId(),
                eventDto.maxPlaces(),
                eventDto.occupiedPlaces(),
                LocalDateTime.parse(eventDto.date(), DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                eventDto.cost(),
                eventDto.duration(),
                eventDto.locationId(),
                EventStatus.valueOf(eventDto.status())
        );
    }

    public Event from(EventCreateRequestDto eventDto) {
        return new Event(
                null,
                eventDto.name(),
                null,
                eventDto.maxPlaces(),
                null,
                eventDto.date(),
                eventDto.cost(),
                eventDto.duration(),
                eventDto.locationId(),
                null
        );
    }

    public Event from(EventUpdateRequestDto eventDto) {
        return new Event(
                null,
                eventDto.name(),
                null,
                eventDto.maxPlaces(),
                null,
                eventDto.date(),
                eventDto.cost(),
                eventDto.duration(),
                eventDto.locationId(),
                null
        );
    }


    public EventSearchFilter from(EventSearchRequestDto eventDto) {

        return new EventSearchFilter(
                eventDto.name(),
                eventDto.placesMin(),
                eventDto.placesMax(),
                eventDto.dateStartAfter() == null ?
                        null :
                        LocalDateTime.parse(eventDto.dateStartAfter(), DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                eventDto.dateStartBefore() == null ?
                        null :
                        LocalDateTime.parse(eventDto.dateStartBefore(), DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                eventDto.costMin(),
                eventDto.costMax(),
                eventDto.durationMin(),
                eventDto.durationMax(),
                eventDto.locationId(),
                eventDto.eventStatus() == null ? null : EventStatus.valueOf(eventDto.eventStatus())
        );
    }


}
