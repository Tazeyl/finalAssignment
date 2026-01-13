package sokolov.spring.finalassignment.location;

import org.springframework.stereotype.Component;

@Component
public class LocationEntityConverter {

    public LocationEntity toEntity(Location location) {
        return new LocationEntity(location.id(), location.name(), location.address(), location.capacity(), location.description());
    }

    public Location fromEntity(LocationEntity locationEntity) {
        return new Location(locationEntity.getId(), locationEntity.getName(), locationEntity.getAddress(), locationEntity.getCapacity(), locationEntity.getDescription());
    }

}
