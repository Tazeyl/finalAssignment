package sokolov.spring.finalassignment.notification;

import jakarta.validation.constraints.Null;
import org.springframework.stereotype.Service;
import sokolov.spring.finalassignment.events.db.registration.RegistrationEntity;
import sokolov.spring.finalassignment.events.db.registration.RegistrationEventRepository;
import sokolov.spring.finalassignment.events.domain.Event;

import sokolov.spring.finalassignment.events.domain.NotificationType;
import sokolov.spring.finalassignment.security.jwt.AuthenticationService;
import sokolov.spring.finalassignment.users.domain.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class NotificationService {

    private final NotificationSender notificationSender;
    private final AuthenticationService authenticationService;
    private final RegistrationEventRepository registrationEventRepository;

    public NotificationService(NotificationSender notificationSender,
                               AuthenticationService authenticationService,
                               RegistrationEventRepository registrationEventRepository) {
        this.notificationSender = notificationSender;
        this.authenticationService = authenticationService;
        this.registrationEventRepository = registrationEventRepository;
    }

    public void sendNotification(Event oldEvent, Event newEvent, NotificationType type, User user) {
        NotificationEvent notify = null;

        switch (type) {
            case UPDATE:
            case STATUSCHANGE:
                notify = fromUpdate(oldEvent, newEvent, user);
                break;
            case DELETE:
                notify = fromDelete(oldEvent, null, user);
                break;
        }


        notificationSender.sendNotificationEvent(notify);
    }

    private NotificationEvent fromUpdate (Event oldEvent, Event newEvent, User user){

        var users =
                registrationEventRepository.findAllByEventId(newEvent.id()).stream()
                        .map(RegistrationEntity::getUserId)
                        .toList();

        return new NotificationEvent(
                newEvent.id(),
                user == null ? null:user.id(),
                newEvent.ownerId(),
                oldEvent.name(),
                newEvent.name(),
                oldEvent.maxPlaces(),
                newEvent.maxPlaces(),
                oldEvent.date().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                newEvent.date().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                oldEvent.cost(),
                newEvent.cost(),
                oldEvent.duration(),
                newEvent.duration(),
                oldEvent.locationId(),
                newEvent.locationId(),
                oldEvent.status().toString(),
                newEvent.status().toString(),
                users,
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }


    private NotificationEvent fromDelete (Event oldEvent, @Null Event newEvent, User user){
        var users =
                registrationEventRepository.findAllByEventId(oldEvent.id()).stream()
                        .map(RegistrationEntity::getUserId)
                        .toList();

        return new NotificationEvent(
                oldEvent.id(),
                user == null? null:user.id(),
                null,
                oldEvent.name(),
                null,
                oldEvent.maxPlaces(),
                null,
                oldEvent.date().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                null,
                oldEvent.cost(),
                null,
                oldEvent.duration(),
                null,
                oldEvent.locationId(),
                null,
                oldEvent.status().toString(),
                null,
                users,
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)


        );
    }
}
