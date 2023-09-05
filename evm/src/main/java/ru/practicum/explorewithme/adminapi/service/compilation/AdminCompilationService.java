package ru.practicum.explorewithme.adminapi.service.compilation;

import ru.practicum.explorewithme.common.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.common.dto.compilation.NewCompilationDto;
import ru.practicum.explorewithme.common.dto.compilation.UpdateCompilationRequest;

public interface AdminCompilationService {
    CompilationDto save(NewCompilationDto compilationDto);

    void delete(int compId);

    CompilationDto update(int compId, UpdateCompilationRequest compilationDto);
}
