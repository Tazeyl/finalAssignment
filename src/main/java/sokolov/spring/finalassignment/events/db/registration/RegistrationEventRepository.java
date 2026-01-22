package sokolov.spring.finalassignment.events.db.registration;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistrationEventRepository extends JpaRepository<RegistrationEntity, Long> {
    List<RegistrationEntity> findAllByUserId(Long userId);

    RegistrationEntity findAllByUserIdAndEventId(Long userId, Long eventId);

    boolean existsByUserIdAndEventId(
            Long userId, Long eventId
    );

    List<RegistrationEntity> findAllByEventId(Long eventId);
}
