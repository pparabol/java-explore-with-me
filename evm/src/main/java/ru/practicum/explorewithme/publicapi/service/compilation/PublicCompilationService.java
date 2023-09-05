package ru.practicum.explorewithme.publicapi.service.compilation;

import ru.practicum.explorewithme.common.dto.compilation.CompilationDto;

import java.util.List;

public interface PublicCompilationService {
    List<CompilationDto> getAll(Boolean pinned, int from, int size);

    CompilationDto get(int compId);
}
