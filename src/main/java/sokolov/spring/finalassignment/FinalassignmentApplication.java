package sokolov.spring.finalassignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class FinalassignmentApplication {
    public static void main(String[] args) {
        SpringApplication.run(FinalassignmentApplication.class, args);
    }
}