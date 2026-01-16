package sokolov.spring.finalassignment.events.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import sokolov.spring.finalassignment.events.domain.EventStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, Long>, JpaSpecificationExecutor<EventEntity> {


    @Query("select e from EventEntity e where e.ownerId = :ownerId")
    List<EventEntity> findByOwnerId(Long ownerId);

    @Query(value = "select e from EventEntity e where e.status = :eventStatus and e.date < :date")
    List<EventEntity> getAllWillStart(EventStatus eventStatus, LocalDateTime date);

    @Query(value = "select e from EventEntity e where e.status = :eventStatus and e.date + e.duration MINUTE  < :date")
    List<EventEntity> getAllWillFinish(EventStatus eventStatus, LocalDateTime date);
}
