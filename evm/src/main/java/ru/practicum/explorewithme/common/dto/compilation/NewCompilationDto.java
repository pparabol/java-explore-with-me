package ru.practicum.explorewithme.common.dto.compilation;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Value
@Builder
public class NewCompilationDto {
    Set<Long> events;

    boolean pinned;

    @NotBlank(message = "must not be blank")
    @Size(max = 50, min = 1, message = "must be between 1 and 50 characters")
    String title;
}
