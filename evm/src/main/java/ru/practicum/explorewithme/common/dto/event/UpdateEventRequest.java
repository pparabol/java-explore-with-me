package ru.practicum.explorewithme.common.dto.event;

import lombok.Builder;
import lombok.Value;
import ru.practicum.explorewithme.common.model.Location;

import javax.validation.constraints.Future;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Value
@Builder
public class UpdateEventRequest {

    @Size(max = 2000, min = 20, message = "must be between 20 and 2000 characters")
    String annotation;

    Integer category;

    @Size(max = 7000, min = 20, message = "must be between 20 and 7000 characters")
    String description;

    @Future(message = "must be in future")
    LocalDateTime eventDate;

    Location location;

    Boolean paid;

    Integer participantLimit;

    Boolean requestModeration;

    StateAction stateAction;

    @Size(max = 120, min = 3, message = "must be between 3 and 120 characters")
    String title;
}
