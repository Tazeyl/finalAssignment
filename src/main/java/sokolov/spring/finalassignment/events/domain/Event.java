package sokolov.spring.finalassignment.events.domain;

import java.time.LocalDateTime;

public record Event(Long id,
                    String name,
                    Long ownerId,
                    Integer maxPlaces,
                    Integer occupiedPlaces,
                    LocalDateTime date,
                    Integer cost,
                    Integer duration,
                    Long locationId,
                    EventStatus status) {
}
