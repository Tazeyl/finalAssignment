package sokolov.spring.finalassignment.users.api;

public record UserDto(
        Long id,
        String login,
        Integer age,
        String role
) {
}
