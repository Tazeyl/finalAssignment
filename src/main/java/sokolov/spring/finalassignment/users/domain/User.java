package sokolov.spring.finalassignment.users.domain;

public record User(
        Long id,
        String login,
        UserRole role,
        Integer age

) {
}
