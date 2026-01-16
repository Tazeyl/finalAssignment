package sokolov.spring.finalassignment.events.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record EventUpdateRequestDto(
        @NotBlank(message = "Название мероприятия обязательно")
        String name,
        @NotNull(message = "Максимальное количество мест на мероприятии обязательно")
        @Min(value = 1, message = "Максимальное количество участников должно быть больше 0")
        Integer maxPlaces,
        @NotNull(message = "Дата и время проведения обязательны")
        @Future(message = "Дата мероприятия должна быть в будущем")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime date,
        @NotNull(message = "Стоимость обязательна")
        @Min(value = 0, message = "Стоимость не может быть отрицательной")
        Integer cost,
        @NotNull(message = "Длительность обязательна")
        @Min(value = 30, message = "Длительность не может быть меньше 30 минут")
        Integer duration,
        @NotNull(message = "Идентификатор локации обязателен")
        Long locationId

) {
}
