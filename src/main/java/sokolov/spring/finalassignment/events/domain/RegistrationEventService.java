package sokolov.spring.finalassignment.events.domain;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sokolov.spring.finalassignment.events.db.EventEntityConverter;
import sokolov.spring.finalassignment.events.db.registration.RegistrationEntity;
import sokolov.spring.finalassignment.events.db.registration.RegistrationEventRepository;
import sokolov.spring.finalassignment.exception.BusinessException;
import sokolov.spring.finalassignment.security.jwt.AuthenticationService;
import sokolov.spring.finalassignment.users.domain.User;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class RegistrationEventService {

    private final EventEntityConverter eventEntityConverter;
    private final AuthenticationService authenticationService;
    private final RegistrationEventRepository registrationEventRepository;
    private final EventService eventService;

    public RegistrationEventService(EventEntityConverter eventEntityConverter,
                                    AuthenticationService authenticationService,
                                    RegistrationEventRepository registrationEventRepository,
                                    EventService eventService) {
        this.registrationEventRepository = registrationEventRepository;
        this.eventService = eventService;
        this.eventEntityConverter = eventEntityConverter;
        this.authenticationService = authenticationService;
    }

    @Transactional
    @Modifying
    public void registerCurrentUserByEvent(Long evenId) {
     User user = authenticationService.getCurrentUser();
        Event event = eventService.getEventById(evenId);
        if (registrationEventRepository.existsByUserIdAndEventId(user.id(), evenId)){
            throw new BusinessException("Вы уже зарегестированы на мероприятие с id = %s".formatted(event.id()));
        }

        if (event.occupiedPlaces() >= event.maxPlaces() ){
            throw new BusinessException("На мероприятия уже зарегистрировано максимальное количество");
        }

        RegistrationEntity registrationEntity = new RegistrationEntity();
        registrationEntity.setEvent(eventEntityConverter.to(event));
        registrationEntity.setUserId(user.id());
        registrationEntity.setCreateDate(LocalDateTime.now());

        registrationEventRepository.save(registrationEntity);
        eventService.addRegistrationByEventId(evenId);

    }

    public List<Event> getAllEventRegistrationByCurrentUser() {

        User user = authenticationService.getCurrentUser();
        return registrationEventRepository.findAllByUserId(user.id()).stream()
                .map(RegistrationEntity::getEvent)
                .map(eventEntityConverter::from)
                .toList();

    }

    public void deleteRegistrationCurrentUserByEvent(Long eventId) {

        User user = authenticationService.getCurrentUser();
        RegistrationEntity registrationEntity =
                registrationEventRepository.findAllByUserIdAndEventId(user.id(), eventId);
        if (registrationEntity == null){
            throw new BusinessException("Вы не зарегестрированы на событие id = %s".formatted(eventId));
        }
        registrationEventRepository.delete(registrationEntity);
        eventService.deleteRegistrationByEventId(eventId);

    }
}
