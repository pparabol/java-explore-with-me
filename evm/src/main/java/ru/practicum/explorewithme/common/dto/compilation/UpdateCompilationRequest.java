package ru.practicum.explorewithme.common.dto.compilation;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Size;
import java.util.Set;

@Value
@Builder
public class UpdateCompilationRequest {
    Set<Long> events;

    Boolean pinned;

    @Size(max = 50, min = 1, message = "must be between 1 and 50 characters")
    String title;
}
