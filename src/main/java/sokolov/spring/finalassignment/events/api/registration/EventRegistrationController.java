package sokolov.spring.finalassignment.events.api.registration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sokolov.spring.finalassignment.events.api.EventDto;
import sokolov.spring.finalassignment.events.api.EventDtoConverter;
import sokolov.spring.finalassignment.events.domain.RegistrationEventService;

import java.util.List;

@RestController
@RequestMapping(path = "/events/registration")
public class EventRegistrationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventRegistrationController.class);

    private final RegistrationEventService eventRegistrationService;
    private final EventDtoConverter eventDtoConverter;

    public EventRegistrationController(RegistrationEventService eventRegistrationService,
                                       EventDtoConverter eventDtoConverter) {
        this.eventRegistrationService = eventRegistrationService;
        this.eventDtoConverter = eventDtoConverter;
    }

    @PostMapping(path = "/{id}")
    @PreAuthorize("hasAuthority(\"USER\")")
    public ResponseEntity<Void> registerUserByEvent(
            @PathVariable Long id
    ){
        LOGGER.info("Get register by current user event id = {}", id);
        eventRegistrationService.registerCurrentUserByEvent(id);
        return ResponseEntity.status(HttpStatus.OK).build();

    }

    @DeleteMapping(path = "/cancel/{id}")
    @PreAuthorize("hasAuthority(\"USER\")")
    public ResponseEntity<Void> deleteRegistrationUserByEvent(
            @PathVariable Long id
    ){
        LOGGER.info("Get delete registration by current user event id = {}", id);
        eventRegistrationService.deleteRegistrationCurrentUserByEvent(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(path = "/my")
    @PreAuthorize("hasAuthority(\"USER\")")
    public ResponseEntity<List<EventDto>> getAllEventRegistrationByCurrentUser(
    ){
        LOGGER.info("Get all registration events by current user");
        return ResponseEntity.status(HttpStatus.OK).body(
                eventRegistrationService.getAllEventRegistrationByCurrentUser().stream()
                        .map(eventDtoConverter::to)
                        .toList()
        );

    }
}
