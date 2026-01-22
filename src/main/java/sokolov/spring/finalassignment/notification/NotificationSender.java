package sokolov.spring.finalassignment.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationSender {

    private static final Logger LOGGER  = LoggerFactory.getLogger(NotificationSender.class);
    private final KafkaTemplate<Long, NotificationEvent> kafkaTemplate;
    private final String topics;


    public NotificationSender(KafkaTemplate<Long, NotificationEvent> kafkaTemplate
    , @Value("${kafka.topics.notification}") String topics ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topics = topics;
    }

    public void sendNotificationEvent(NotificationEvent notificationEvent){
        LOGGER.info("Sending event: event = {}", notificationEvent);
        if (notificationEvent == null){
            return;
        }
        var result = kafkaTemplate.send(
                topics,
                notificationEvent.eventId(),
                notificationEvent
        );
        result.thenAccept( sendResult ->{
            LOGGER.info("Kafka OK");
        });
    }
}
