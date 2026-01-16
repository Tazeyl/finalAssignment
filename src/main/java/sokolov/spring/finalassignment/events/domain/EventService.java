package sokolov.spring.finalassignment.events.domain;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import sokolov.spring.finalassignment.events.db.EventEntity;
import sokolov.spring.finalassignment.events.db.EventEntityConverter;
import sokolov.spring.finalassignment.events.db.EventRepository;
import sokolov.spring.finalassignment.exception.BusinessException;
import sokolov.spring.finalassignment.location.Location;
import sokolov.spring.finalassignment.location.LocationService;
import sokolov.spring.finalassignment.security.jwt.AuthenticationService;
import sokolov.spring.finalassignment.users.domain.User;
import sokolov.spring.finalassignment.users.domain.UserRole;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
public class EventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventService.class);

    private static final String ENTITY_NOT_FOUND = "Not found Event by id = %s";

    private final EventRepository eventRepository;
    private final LocationService locationService;
    private final EventEntityConverter eventEntityConverter;
    private final AuthenticationService authenticationService;

    public EventService(EventRepository eventRepository,
                        LocationService locationService,
                        EventEntityConverter eventEntityConverter,
                        AuthenticationService authenticationService) {
        this.eventRepository = eventRepository;
        this.locationService = locationService;
        this.eventEntityConverter = eventEntityConverter;
        this.authenticationService = authenticationService;
    }

    @Transactional
    public Event createEvent(Event event) {
        Location location = getLocationById(event.locationId());
        if (location.capacity() < event.maxPlaces()){
            throw new BusinessException("Вместимость локации меньше максимальной вместимости мероприятия");
        }

        EventEntity eventEntityToSave = eventEntityConverter.to(event);
        User currentUser = authenticationService.getCurrentUser();
        eventEntityToSave.setOwnerId(currentUser.id());
        eventEntityToSave.setOccupiedPlaces(0);
        eventEntityToSave.setStatus(EventStatus.WAIT_START);

        EventEntity savedEventEntity = eventRepository.save(eventEntityToSave);

        return eventEntityConverter.from(savedEventEntity);
    }

    @Transactional
    @Modifying
    public void deleteEvent(Long id) {

        User currentUser = authenticationService.getCurrentUser();

        EventEntity eventEntity = getEventEntityById(id);

        if (!Objects.equals(currentUser.role(), UserRole.ADMIN)
                && !Objects.equals(eventEntity.getOwnerId(), currentUser.id())
        ){
            throw new BusinessException("Удалить событие может только владелец (или администратор)");
        }


        if (!Objects.equals(eventEntity.getStatus(), EventStatus.WAIT_START)){
            throw new BusinessException("Невозможно удалить событие в статусе, отличающееся от WAIT_START");
        }

        eventEntity.setStatus(EventStatus.CANCELLED);
        eventRepository.save(eventEntity);

    }


    public Event getEventById(Long id) {
        return eventEntityConverter.from(getEventEntityById(id));
    }

    private EventEntity getEventEntityById(Long id){
        if (!eventRepository.existsById(id)){
            throw new EntityNotFoundException(ENTITY_NOT_FOUND.formatted(id));
        }
        return  eventRepository.getReferenceById(id);
    }

    @Transactional
    @Modifying
    public Event updateEvent(Long eventId, Event event) {
        User currentUser = authenticationService.getCurrentUser();
        EventEntity eventEntity = getEventEntityById(eventId);

        if (!Objects.equals(currentUser.role(), UserRole.ADMIN)
                && !Objects.equals(eventEntity.getOwnerId(), currentUser.id())
        ){
            throw new BusinessException("Изменять событие может только владелец (или администратор)");
        }


        if (!Objects.equals(eventEntity.getStatus(), EventStatus.WAIT_START)){
            throw new BusinessException("Невозможно изменить событие в статусе, отличающееся от WAIT_START");
        }
        Integer newMaxPlaces = event.maxPlaces();

        if (newMaxPlaces < eventEntity.getOccupiedPlaces()){
            throw new BusinessException(
                    "Невозможно изменить событие. Максимальное количество меньше количества уже зарегистрированных"
            );
        }

        eventEntity.setName(event.name());
        eventEntity.setDate(event.date());
        eventEntity.setCost(event.cost());
        eventEntity.setDuration(event.duration());
        if (!Objects.equals(event.locationId(), eventEntity.getLocationId())){
            // change location
            Location location = getLocationById(event.locationId());
            if (location.capacity() < newMaxPlaces){
                throw new BusinessException("Вместимость локации меньше максимальной вместимости мероприятия");
            }

            eventEntity.setLocationId(event.locationId());
        }
        eventEntity.setMaxPlaces(newMaxPlaces);
        EventEntity saved = eventRepository.save(eventEntity);

        return eventEntityConverter.from(saved);

    }


    private Location getLocationById(Long id){
        return locationService.getById(id);
    }

    public List<Event> getEventByFilter(EventSearchFilter filter) {

        return eventRepository.findAll(buildSpecification(filter)).stream()
                .map(eventEntityConverter::from)
                .toList();
    }


    /**
     * Построение спецификации для динамических запросов
     */
    private Specification<EventEntity> buildSpecification(EventSearchFilter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Фильтр по названию (полное совпадение, case-insensitive)
            if (filter.name() != null && StringUtils.hasText(filter.name())) {
                predicates.add(criteriaBuilder.equal(
                        root.get("name"), filter.name()
                ));
            }

            // Фильтр по минимальному количеству мест
            if (filter.placesMin() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("availablePlaces"), filter.placesMin()
                ));
            }

            // Фильтр по максимальному количеству мест
            if (filter.placesMax() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("availablePlaces"), filter.placesMax()
                ));
            }

            // Фильтр по дате начала (после указанной даты)
            if (filter.dateStartAfter() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("date"), filter.dateStartAfter()
                ));
            }

            // Фильтр по дате начала (до указанной даты)
            if (filter.dateStartBefore() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("date"), filter.dateStartBefore()
                ));
            }

            // Фильтр по минимальной стоимости
            if (filter.costMin() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("cost"), filter.costMin()
                ));
            }

            // Фильтр по максимальной стоимости
            if (filter.costMax() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("cost"), filter.costMax()
                ));
            }

            // Фильтр по минимальной длительности (в минутах)
            if (filter.durationMin() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("durationMinutes"), filter.durationMin()
                ));
            }

            // Фильтр по максимальной длительности (в минутах)
            if (filter.durationMax() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("durationMinutes"), filter.durationMax()
                ));
            }

            // Фильтр по локации
            if (filter.locationId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("locationId"), filter.locationId()
                ));
            }

            // Фильтр по статусу события
            if (filter.eventStatus() != null && StringUtils.hasText(filter.eventStatus().toString())) {
                    predicates.add(criteriaBuilder.equal(
                            root.get("status"), filter.eventStatus()
                    ));
            }

            // Сортировка по дате начала (по умолчанию)
            query.orderBy(criteriaBuilder.asc(root.get("date")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public List<Event> getEventByCurrentUser() {

        return eventRepository.findByOwnerId(authenticationService.getCurrentUser().id()).stream()
                .map(eventEntityConverter::from)
                .toList();


    }

    @Transactional
    @Modifying
    public void addRegistrationByEventId(Long eventId) {
        EventEntity eventEntity = getEventEntityById(eventId);
        if (Objects.equals(eventEntity.getStatus(), EventStatus.FINISHED)){
            throw new BusinessException("Невозможно зарегистрироваться на мероприятие. Мероприятие завершено");
        }
        if (Objects.equals(eventEntity.getStatus(), EventStatus.CANCELLED)){
            throw new BusinessException("Невозможно зарегистрироваться на мероприятие. Мероприятие отменено");
        }
        if (Objects.equals(eventEntity.getStatus(), EventStatus.STARTED)){
            throw new BusinessException("Невозможно зарегистрироваться на мероприятие. Мероприятие уже началось");
        }

        if ( eventEntity.getOccupiedPlaces() >= eventEntity.getMaxPlaces()){
            throw new BusinessException("На мероприятия уже зарегистрировано максимальное количество посетителей");
        }
        eventEntity.setOccupiedPlaces(eventEntity.getOccupiedPlaces()+1);


    }

    @Transactional
    @Modifying
    public void deleteRegistrationByEventId(Long eventId) {
        EventEntity eventEntity = getEventEntityById(eventId);
        if (Objects.equals(eventEntity.getStatus(), EventStatus.FINISHED)){
            throw new BusinessException("Невозможно отменить регистрацию на мероприятие. Мероприятие завершено");
        }
        if (Objects.equals(eventEntity.getStatus(), EventStatus.CANCELLED)){
            throw new BusinessException("Невозможно отменить регистрацию на мероприятие. Мероприятие отменено");
        }
        if (Objects.equals(eventEntity.getStatus(), EventStatus.STARTED)){
            throw new BusinessException("Невозможно отменить регистрацию на мероприятие. Мероприятие уже началось");
        }

        if (eventEntity.getOccupiedPlaces()<= 0){
            // Проблемы с ядром регистрации
            throw new IllegalArgumentException("На мероприятие никто не зарегистрирован");
        }
        eventEntity.setOccupiedPlaces(eventEntity.getOccupiedPlaces()-1);
    }

    @Transactional
    @Modifying
    public void scheduleEvents() {
        List<EventEntity> scheduledEvent = eventRepository.getAllWillStart(EventStatus.WAIT_START, LocalDateTime.now());
        scheduledEvent.forEach(t -> moveToNewStatusEvent(t, EventStatus.STARTED));

        scheduledEvent = eventRepository.getAllWillFinish(EventStatus.STARTED, LocalDateTime.now());
        scheduledEvent.forEach(t -> moveToNewStatusEvent(t, EventStatus.FINISHED));
    }

    private void moveToNewStatusEvent(EventEntity eventEntity, EventStatus newEventStatus) {
        if (Objects.equals(eventEntity.getStatus(), EventStatus.FINISHED) ||
                Objects.equals(eventEntity.getStatus(), EventStatus.CANCELLED)){
            throw new IllegalArgumentException("Попытка перевести мероприятие в некорректный статус");
        }
        if (Objects.equals(eventEntity.getStatus(), newEventStatus)){
            LOGGER.error("Попытка перевести event id = {} в тот же статус {}", eventEntity.getId(), newEventStatus);
            return;
        }

        eventEntity.setStatus(newEventStatus);

    }
}
