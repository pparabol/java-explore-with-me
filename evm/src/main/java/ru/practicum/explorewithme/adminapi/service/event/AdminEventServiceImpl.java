package ru.practicum.explorewithme.adminapi.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.common.dto.event.EventFullDto;
import ru.practicum.explorewithme.common.dto.event.EventState;
import ru.practicum.explorewithme.common.dto.event.UpdateEventRequest;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.exception.ValidationException;
import ru.practicum.explorewithme.common.mapper.EventMapper;
import ru.practicum.explorewithme.common.model.Category;
import ru.practicum.explorewithme.common.model.Event;
import ru.practicum.explorewithme.common.repository.CategoryRepository;
import ru.practicum.explorewithme.common.repository.EventRepository;
import ru.practicum.explorewithme.adminapi.util.SearchEventsAdminParams;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdminEventServiceImpl implements AdminEventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;

    @Override
    public List<EventFullDto> getByCriteria(SearchEventsAdminParams params, int from, int size) {
        PageRequest pageRequest = PageRequest.of(
                from / size,
                size,
                Sort.by(Sort.Direction.ASC, "id")
        );
        return eventRepository.findEventsByCriteria(params.getUsers(),
                        params.getCategories(),
                        params.getStates(),
                        params.getRangeStart(),
                        params.getRangeEnd(),
                        pageRequest).stream()
                .map(eventMapper::toFullDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullDto update(long eventId, UpdateEventRequest eventDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new NotFoundException((String.format("Event with id=%d was not found", eventId))));
        if (!event.getState().equals(EventState.PENDING)) {
            throw new ValidationException("Cannot update the event " +
                    "because it's not in the right state: " + event.getState());
        }
        if (eventDto.getEventDate() != null) {
            validateDate(eventDto.getEventDate());
            event.setEventDate(eventDto.getEventDate());
        }

        if (eventDto.getStateAction() != null) {
            switch (eventDto.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }
        if (eventDto.getCategory() != null) {
            event.setCategory(findCategoryOrThrowException(eventDto.getCategory()));
        }
        if (eventDto.getAnnotation() != null) {
            event.setAnnotation(eventDto.getAnnotation());
        }
        if (eventDto.getDescription() != null) {
            event.setDescription(eventDto.getDescription());
        }
        if (eventDto.getLocation() != null) {
            event.setLocation(eventDto.getLocation());
        }
        if (eventDto.getPaid() != null) {
            event.setPaid(eventDto.getPaid());
        }
        if (eventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }
        if (eventDto.getRequestModeration() != null) {
            event.setRequestModeration(eventDto.getRequestModeration());
        }
        if (eventDto.getTitle() != null) {
            event.setTitle(eventDto.getTitle());
        }

        Event updated = eventRepository.save(event);
        log.info("Admin updated the event: {}", updated);
        return eventMapper.toFullDto(updated);
    }

    private void validateDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ValidationException("Event date can not be earlier than in 1 hour");
        }
    }

    private Category findCategoryOrThrowException(int catId) {
        return categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException(String.format("Category with id=%d was not found", catId)));
    }
}
