package sokolov.spring.finalassignment.notification;

import java.util.List;

public record NotificationEvent(

        Long eventId,
        Long changedUserId,
        Long ownerId,
        String oldName,
        String newName,
        Integer oldMaxPlaces,
        Integer newMaxPlaces,
        String oldDate,
        String newDate,
        Integer oldCost,
        Integer newCost,
        Integer oldDuration,
        Integer newDuration,
        Long oldLocationId,
        Long newLocationId,
        String oldStatus,
        String newStatus,
        List<Long> registrUsersId,
        String createdAt
) {
}
