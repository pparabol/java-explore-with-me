package ru.practicum.explorewithme.adminapi.util;

import lombok.Builder;
import lombok.Value;
import ru.practicum.explorewithme.common.dto.event.EventState;

import java.time.LocalDateTime;
import java.util.Set;

@Value
@Builder
public class SearchEventsAdminParams {
    Set<Long> users;
    Set<EventState> states;
    Set<Integer> categories;
    LocalDateTime rangeStart;
    LocalDateTime rangeEnd;
}
