package ru.practicum.explorewithme.publicapi.service.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.common.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.common.mapper.CompilationMapper;
import ru.practicum.explorewithme.common.repository.CompilationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicCompilationServiceImpl implements PublicCompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    @Override
    public List<CompilationDto> getAll(Boolean pinned, int from, int size) {
        PageRequest pageRequest = PageRequest.of(
                from / size,
                size,
                Sort.by(Sort.Direction.ASC, "id")
        );
        return compilationRepository.findCompilations(pinned, pageRequest).stream()
                .map(compilationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto get(int compId) {
        return compilationRepository.findById(compId)
                .map(compilationMapper::toDto)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Compilation with id=%d was not found", compId)));
    }
}
