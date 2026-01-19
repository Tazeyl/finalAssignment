package sokolov.spring.finalassignment.events.api;

public record EventDto(
        Long id,
        String name,
        Long ownerId,
        Integer maxPlaces,
        Integer occupiedPlaces,
        String date,
        Integer cost,
        Integer duration,
        Long locationId,
        String status
) {
}
