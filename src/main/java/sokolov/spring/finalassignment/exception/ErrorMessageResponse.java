package sokolov.spring.finalassignment.exception;

import java.time.LocalDateTime;

public record ErrorMessageResponse(String message,
                                   String detailedMessage,
                                   LocalDateTime dateTime) {
}
