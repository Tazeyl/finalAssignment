package sokolov.spring.finalassignment.location;

public record Location(Long id,
                       String name,
                       String address,
                       Integer capacity,
                       String description) {
}
