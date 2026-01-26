package sokolov.spring.finalassignment;

import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import sokolov.spring.finalassignment.notification.NotificationEvent;

@Configuration
public class KafkaConfiguration {

    @Bean
    public KafkaTemplate<Long, NotificationEvent> kafkaTemplate(
            KafkaProperties kafkaProperties
    ){
        var props = kafkaProperties.buildProducerProperties();
        ProducerFactory<Long, NotificationEvent> producerFactory = new DefaultKafkaProducerFactory<>(props);
        return new KafkaTemplate<>(producerFactory);
    }
}
