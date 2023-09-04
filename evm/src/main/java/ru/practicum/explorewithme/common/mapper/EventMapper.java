package ru.practicum.explorewithme.common.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.common.dto.category.CategoryDto;
import ru.practicum.explorewithme.common.dto.event.EventFullDto;
import ru.practicum.explorewithme.common.dto.event.EventShortDto;
import ru.practicum.explorewithme.common.dto.event.NewEventDto;
import ru.practicum.explorewithme.common.dto.event.EventState;
import ru.practicum.explorewithme.common.dto.user.UserShortDto;
import ru.practicum.explorewithme.common.model.Category;
import ru.practicum.explorewithme.common.model.Event;
import ru.practicum.explorewithme.common.model.User;
import ru.practicum.explorewithme.mapper.Mapper;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EventMapper {
    private final Mapper<Category, CategoryDto> categoryMapper;

    public Event toNewEntity(NewEventDto eventDto, Category category, User user) {
        return Event.builder()
                .annotation(eventDto.getAnnotation())
                .category(category)
                .createdOn(LocalDateTime.now())
                .description(eventDto.getDescription())
                .eventDate(eventDto.getEventDate())
                .initiator(user)
                .location(eventDto.getLocation())
                .paid(eventDto.isPaid())
                .participantLimit(eventDto.getParticipantLimit())
                .requestModeration(Optional.ofNullable(eventDto.getRequestModeration())
                        .orElse(true))
                .state(EventState.PENDING)
                .title(eventDto.getTitle())
                .build();
    }

    public EventFullDto toFullDto(Event event) {
        return EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(categoryMapper.toDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()))
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public EventShortDto toShortDto(Event event) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(categoryMapper.toDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }
}
