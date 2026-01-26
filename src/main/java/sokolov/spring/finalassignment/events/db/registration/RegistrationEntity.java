package sokolov.spring.finalassignment.events.db.registration;

import jakarta.persistence.*;
import sokolov.spring.finalassignment.events.db.EventEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "registration_entity")
public class RegistrationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private EventEntity event;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    public RegistrationEntity() {
    }

    public RegistrationEntity(Long id, Long userId, EventEntity event, LocalDateTime createDate) {
        this.id = id;
        this.userId = userId;
        this.event = event;
        this.createDate = createDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public EventEntity getEvent() {
        return event;
    }

    public void setEvent(EventEntity event) {
        this.event = event;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }
}
