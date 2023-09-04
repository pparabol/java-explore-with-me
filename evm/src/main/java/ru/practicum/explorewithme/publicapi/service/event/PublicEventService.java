package ru.practicum.explorewithme.publicapi.service.event;

import ru.practicum.explorewithme.common.dto.event.EventFullDto;
import ru.practicum.explorewithme.common.dto.event.EventShortDto;
import ru.practicum.explorewithme.publicapi.util.SearchEventsPublicParams;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface PublicEventService {
    List<EventShortDto> getEventsByCriteria(SearchEventsPublicParams params,
                                            int from,
                                            int size,
                                            HttpServletRequest request);

    EventFullDto get(long eventId, HttpServletRequest request);
}
