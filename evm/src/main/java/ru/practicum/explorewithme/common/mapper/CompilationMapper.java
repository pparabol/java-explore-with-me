package ru.practicum.explorewithme.common.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.common.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.common.dto.compilation.NewCompilationDto;
import ru.practicum.explorewithme.common.model.Compilation;
import ru.practicum.explorewithme.common.model.Event;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompilationMapper {
    private final EventMapper eventMapper;

    public Compilation toNewEntity(NewCompilationDto dto, Set<Event> events) {
        return Compilation.builder()
                .pinned(dto.isPinned())
                .title(dto.getTitle())
                .events(events)
                .build();
    }

    public CompilationDto toDto(Compilation entity) {
        return CompilationDto.builder()
                .events(entity.getEvents().stream()
                        .map(eventMapper::toShortDto)
                        .collect(Collectors.toSet()))
                .id(entity.getId())
                .pinned(entity.getPinned())
                .title(entity.getTitle())
                .build();
    }
}
