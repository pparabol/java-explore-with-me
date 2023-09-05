package ru.practicum.explorewithme.privateapi.service.event;

import ru.practicum.explorewithme.common.dto.event.*;

import java.util.List;

public interface PrivateEventService {
    EventFullDto save(long userId, NewEventDto eventDto);

    EventFullDto get(long userId, long eventId);

    List<EventShortDto> getAll(long userId, int from, int size);

    EventFullDto update(long userId, long eventId, UpdateEventRequest eventDto);
}
