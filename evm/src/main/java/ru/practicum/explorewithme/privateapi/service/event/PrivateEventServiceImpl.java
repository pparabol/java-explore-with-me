package ru.practicum.explorewithme.privateapi.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.common.dto.event.*;
import ru.practicum.explorewithme.common.exception.NotFoundException;
import ru.practicum.explorewithme.common.exception.ValidationException;
import ru.practicum.explorewithme.common.mapper.EventMapper;
import ru.practicum.explorewithme.common.model.Category;
import ru.practicum.explorewithme.common.model.Event;
import ru.practicum.explorewithme.common.model.User;
import ru.practicum.explorewithme.common.repository.CategoryRepository;
import ru.practicum.explorewithme.common.repository.EventRepository;
import ru.practicum.explorewithme.common.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateEventServiceImpl implements PrivateEventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    @Override
    public EventFullDto save(long userId, NewEventDto eventDto) {
        validateDate(eventDto.getEventDate());

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id=%d was not found", userId)));
        Category category = findCategoryOrThrowException(eventDto.getCategory());

        Event event = eventRepository.save(eventMapper.toNewEntity(eventDto, category, user));

        return eventMapper.toFullDto(event);
    }

    @Override
    public EventFullDto get(long userId, long eventId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .map(eventMapper::toFullDto)
                .orElseThrow(() ->
                        new NotFoundException((String.format("Event with id=%d was not found", eventId))));
    }

    @Override
    public List<EventShortDto> getAll(long userId, int from, int size) {
        PageRequest pageRequest = PageRequest.of(
                from / size,
                size,
                Sort.by(Sort.Direction.ASC, "id")
        );
        return eventRepository.findAllByInitiatorId(userId, pageRequest).stream()
                .map(eventMapper::toShortDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullDto update(long userId, long eventId, UpdateEventRequest eventDto) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() ->
                        new NotFoundException((String.format("Event with id=%d was not found", eventId))));
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("Only pending or canceled events can be changed");
        }
        if (eventDto.getEventDate() != null) {
            validateDate(eventDto.getEventDate());
            event.setEventDate(eventDto.getEventDate());
        }

        if (eventDto.getStateAction() != null) {
            switch (eventDto.getStateAction()) {
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
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

        return eventMapper.toFullDto(eventRepository.save(event));
    }

    private void validateDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Event date can not be earlier than in 2 hours");
        }
    }

    private Category findCategoryOrThrowException(int catId) {
        return categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException(String.format("Category with id=%d was not found", catId)));
    }
}
