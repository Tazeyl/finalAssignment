package sokolov.spring.finalassignment.location;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationController {

    private final Logger logger = LoggerFactory.getLogger(LocationController.class);

    private final LocationService locationService;

    private final LocationDtoConverter locationDtoConverter;

    public LocationController(LocationService locationService, LocationDtoConverter locationDtoConverter) {
        this.locationService = locationService;
        this.locationDtoConverter = locationDtoConverter;
    }

    @GetMapping
    public ResponseEntity<List<LocationDto>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAll().stream()
                .map(locationDtoConverter::toDto)
                .toList()
        );
    }

    @PostMapping
    public ResponseEntity<LocationDto> createLocation(
            @Valid @RequestBody LocationDto locationDto
    ) {
        logger.info("create location: {}", locationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(locationDtoConverter.toDto(locationService.save(locationDtoConverter.fromDto(locationDto))));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<LocationDto> updateLocation(
            @PathVariable Long id,
            @Valid @RequestBody LocationDto locationDto) {
        logger.info("update location by id= {}, value:{}", id, locationDto);
        return ResponseEntity.ok(locationDtoConverter.toDto(locationService.update(id, locationDtoConverter.fromDto(locationDto))));

    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteLocation(
            @PathVariable Long id
    ) {
        logger.info("delete location by id= {}", id);
        locationService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<LocationDto> getLocationById(@PathVariable Long id) {
        logger.info("get location by id= {}", id);
        return ResponseEntity.ok(locationDtoConverter.toDto(locationService.getById(id)));
    }
}
