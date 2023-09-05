package ru.practicum.explorewithme.publicapi.util;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Set;

@Value
@Builder
public class SearchEventsPublicParams {
    String text;
    Set<Integer> categories;
    Boolean paid;
    LocalDateTime rangeStart;
    LocalDateTime rangeEnd;
    boolean onlyAvailable;
    Sort sort;
}
