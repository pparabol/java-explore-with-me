package ru.practicum.explorewithme.adminapi.service.event;

import ru.practicum.explorewithme.common.dto.event.EventFullDto;
import ru.practicum.explorewithme.common.dto.event.UpdateEventRequest;
import ru.practicum.explorewithme.adminapi.util.SearchEventsAdminParams;

import java.util.List;

public interface AdminEventService {
    List<EventFullDto> getByCriteria(SearchEventsAdminParams params, int from, int size);

    EventFullDto update(long eventId, UpdateEventRequest eventDto);
}
