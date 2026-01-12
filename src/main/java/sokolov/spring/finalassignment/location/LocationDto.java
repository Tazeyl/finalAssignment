package sokolov.spring.finalassignment.location;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

public record LocationDto(
        @Null
        Long id,
        @NotBlank(message = "name cannot be empty")
        String name,
        @NotBlank(message = "address cannot be empty")
        String address,
        @NotNull(message = "capacity cannot be empty")
        //TODO parameter
        @Min(value = 5, message = "capacity must be greater than 5")
        Integer capacity,

        String description
) {
}
