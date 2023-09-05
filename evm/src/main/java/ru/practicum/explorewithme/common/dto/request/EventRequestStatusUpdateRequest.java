package ru.practicum.explorewithme.common.dto.request;

import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@Builder
public class EventRequestStatusUpdateRequest {
    Set<Long> requestIds;
    RequestStatus status;
}
