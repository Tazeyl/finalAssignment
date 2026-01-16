package sokolov.spring.finalassignment.events.api;

import com.fasterxml.jackson.annotation.JsonFormat;

public record EventSearchRequestDto(

        String name,
        Integer placesMin,
        Integer placesMax,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        String dateStartAfter,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        String dateStartBefore,
        Integer costMin,
        Integer costMax,
        Integer durationMin,
        Integer durationMax,
        Integer locationId,
        String eventStatus

) {
}
