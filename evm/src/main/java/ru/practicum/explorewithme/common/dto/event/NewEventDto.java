package ru.practicum.explorewithme.common.dto.event;

import lombok.Builder;
import lombok.Value;
import ru.practicum.explorewithme.common.model.Location;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Value
@Builder
public class NewEventDto {

    @NotBlank(message = "must not be blank")
    @Size(max = 2000, min = 20, message = "must be between 20 and 2000 characters")
    String annotation;

    @NotNull(message = "must not be null")
    Integer category;

    @NotBlank(message = "must not be blank")
    @Size(max = 7000, min = 20, message = "must be between 20 and 7000 characters")
    String description;

    @NotNull(message = "must not be null")
    @Future(message = "must be in future")
    LocalDateTime eventDate;

    @NotNull(message = "must not be null")
    Location location;

    boolean paid;

    @PositiveOrZero(message = "must not be negative")
    int participantLimit;

    Boolean requestModeration;

    @NotBlank(message = "must not be blank")
    @Size(max = 120, min = 3, message = "must be between 3 and 120 characters")
    String title;
}
