package ru.practicum.explorewithme.common.dto.compilation;

import lombok.Builder;
import lombok.Data;
import ru.practicum.explorewithme.common.dto.event.EventShortDto;

import java.util.Set;

@Data
@Builder
public class CompilationDto {
    private Set<EventShortDto> events;
    private Integer id;
    private boolean pinned;
    private String title;
}
