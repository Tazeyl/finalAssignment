package sokolov.spring.finalassignment.location;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final LocationEntityConverter locationEntityConverter;

    public LocationService(LocationRepository locationRepository, LocationEntityConverter locationEntityConverter) {
        this.locationRepository = locationRepository;
        this.locationEntityConverter = locationEntityConverter;
    }


    public List<Location> getAll() {
        return locationRepository.findAll().stream()
                .map(locationEntityConverter::fromEntity)
                .toList();
    }

    public Location save(@Valid Location location) {
        LocationEntity locationEntity = locationEntityConverter.toEntity(location);
        LocationEntity savedLocationEntity = locationRepository.save(locationEntity);
        return locationEntityConverter.fromEntity(savedLocationEntity);
    }

    public Location update(Long id, @Valid Location location) {
        if (!locationRepository.existsById(id)){
            throw new EntityNotFoundException("Not found Location by id = %s".formatted(id));
        }
        LocationEntity locationEntity = locationRepository.getReferenceById(id);
        locationEntity.setName(location.name());
        locationEntity.setAddress(location.address());
        locationEntity.setCapacity(location.capacity());
        locationEntity.setDescription(location.description());

        locationRepository.save(locationEntity);
        return locationEntityConverter.fromEntity(locationEntity);
    }

    public void delete(Long id) {
        if (!locationRepository.existsById(id)){
            throw new EntityNotFoundException("Not found Location by id = %s".formatted(id));
        }
        locationRepository.deleteById(id);
    }

    public Location getById(Long id) {
        return locationEntityConverter.fromEntity(locationRepository.getReferenceById(id));
    }
}
