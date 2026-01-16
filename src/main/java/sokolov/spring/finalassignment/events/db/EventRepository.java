package sokolov.spring.finalassignment.events.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, Long>, JpaSpecificationExecutor<EventEntity> {


    @Query("select e from EventEntity e where e.ownerId = :ownerId")
    List<EventEntity> findByOwnerId(Long ownerId);
}
