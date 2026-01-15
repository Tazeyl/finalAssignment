package sokolov.spring.finalassignment.users;

import jakarta.validation.constraints.NotBlank;

public record SigninRequest(
        @NotBlank
        String login,
        @NotBlank
        String password

) {
}
