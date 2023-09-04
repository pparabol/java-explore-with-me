package ru.practicum.explorewithme.adminapi.service.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.common.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.common.dto.compilation.NewCompilationDto;
import ru.practicum.explorewithme.common.dto.compilation.UpdateCompilationRequest;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.common.mapper.CompilationMapper;
import ru.practicum.explorewithme.common.model.Compilation;
import ru.practicum.explorewithme.common.model.Event;
import ru.practicum.explorewithme.common.repository.CompilationRepository;
import ru.practicum.explorewithme.common.repository.EventRepository;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdminCompilationServiceImpl implements AdminCompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;

    @Transactional
    @Override
    public CompilationDto save(NewCompilationDto compilationDto) {
        Set<Event> events = new HashSet<>();
        if (compilationDto.getEvents() != null) {
            events = eventRepository.findAllByIdIn(compilationDto.getEvents());
        }
        Compilation compilation = compilationRepository.save(compilationMapper.toNewEntity(compilationDto, events));
        log.info("Saved compilation: {}", compilation);
        return compilationMapper.toDto(compilation);
    }

    @Transactional
    @Override
    public void delete(int compId) {
        findCompilationOrThrowException(compId);
        compilationRepository.deleteById(compId);
        log.info("Compilation with id={} has been deleted", compId);
    }

    @Transactional
    @Override
    public CompilationDto update(int compId, UpdateCompilationRequest compilationDto) {
        Compilation compilation = findCompilationOrThrowException(compId);

        if (compilationDto.getEvents() != null) {
            Set<Event> events = eventRepository.findAllByIdIn(compilationDto.getEvents());
            compilation.setEvents(events);
        }
        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }
        if (compilationDto.getTitle() != null) {
            compilation.setTitle(compilationDto.getTitle());
        }

        Compilation updated = compilationRepository.save(compilation);
        log.info("Updated compilation: {}", updated);
        return compilationMapper.toDto(updated);
    }

    private Compilation findCompilationOrThrowException(int compId) {
        return compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException((String.format("Compilation with id=%d was not found", compId))));
    }
}
