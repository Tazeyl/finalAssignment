package sokolov.spring.finalassignment.events.api;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sokolov.spring.finalassignment.events.domain.EventService;

import java.util.List;

@RestController
@RequestMapping(path = "/events")
public class EventsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventsController.class);

    private final EventService eventService;
    private final EventDtoConverter eventDtoConverter;

    public EventsController(EventService eventService, EventDtoConverter eventDtoConverter) {
        this.eventService = eventService;
        this.eventDtoConverter = eventDtoConverter;
    }

    /**
     * Создание нового мероприятия. Allowed roles=[USER]
     */
    @PostMapping
    @PreAuthorize("hasAuthority(\"USER\")")
    public ResponseEntity<EventDto> createEvent(
            @RequestBody @Valid EventCreateRequestDto createRequestDto
    ) {

        LOGGER.info("Get create event request:{}", createRequestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(eventDtoConverter.to(eventService.createEvent(eventDtoConverter.from(createRequestDto))));

    }


    /**
     * Удаление мероприятия. Allowed roles=[USER, ADMIN]
     */
    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasAnyAuthority(\"USER\", \"ADMIN\")")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long id
    ) {
        LOGGER.info("Get delete event request:{}", id);
        eventService.deleteEvent(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    /**
     * Получение мероприятия по ID. Allowed roles=[USER, ADMIN]
     */
    @GetMapping(path = "/{id}")
    @PreAuthorize("hasAnyAuthority(\"USER\", \"ADMIN\")")
    public ResponseEntity<EventDto> getEvent(
            @PathVariable Long id
    ) {
        LOGGER.info("Get event request:{}", id);
        return ResponseEntity.status(HttpStatus.OK).body(eventDtoConverter.to(eventService.getEventById(id)));
    }

    /**
     * Обновление мероприятия. Allowed roles=[USER, ADMIN]
     */
    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAnyAuthority(\"USER\", \"ADMIN\")")
    public ResponseEntity<EventDto> updateEvent(
            @PathVariable Long id,
            @RequestBody @Valid EventUpdateRequestDto eventUpdateRequestDto
    ) {
        LOGGER.info("Get event update request: id = {}, value = {}", id, eventUpdateRequestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        eventDtoConverter.to(
                                eventService.updateEvent(
                                        id, eventDtoConverter.from(eventUpdateRequestDto))
                        )
                );

    }


    /**
     * Поиск мероприятий по фильтру. Allowed roles=[USER, ADMIN]
     */
    @GetMapping(path = "/search")
    @PreAuthorize("hasAnyAuthority(\"USER\", \"ADMIN\")")
    public ResponseEntity<List<EventDto>> getAllEventsByFilter(
            @RequestBody @Valid EventSearchRequestDto eventSearchRequestDto
    ) {
        LOGGER.info("Get event search request: {}", eventSearchRequestDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.getEventByFilter(eventDtoConverter.from(eventSearchRequestDto)).stream()
                        .map(eventDtoConverter::to)
                        .toList()
                );

    }

    /**
     * Получение всех мероприятий, созданных текущим пользователем. Allowed roles=[USER]
     */
    @GetMapping(path = "/my")
    @PreAuthorize("hasAuthority(\"USER\")")
    public ResponseEntity<List<EventDto>> getUserEvents() {
        LOGGER.info("Get event by current user");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.getEventByCurrentUser().stream()
                        .map(eventDtoConverter::to)
                        .toList()
                );

    }


/*
    POST
/events/registrations/{eventId}
    Регистрация пользователя на мероприятие по ID. Allowed roles=[USER]



    DELETE
/events/registrations/cancel/{eventId}
    Отмена регистрации на мероприятие. Allowed roles=[USER]



    GET
/events/registrations/my
    Получение мероприятий, на которые зарегистрирован текущий пользователь. Allowed roles=[USER]
 */
}
