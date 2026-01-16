package sokolov.spring.finalassignment.users.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRegistration(
        @NotBlank
        @Size(min = 5)
        String login,
        @NotBlank
        @Size(min = 5)
        String password,
        @NotNull
        @Min(value = 18)
        Integer age
) {
}
