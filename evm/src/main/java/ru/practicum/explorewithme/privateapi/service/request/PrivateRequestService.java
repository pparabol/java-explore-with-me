package ru.practicum.explorewithme.privateapi.service.request;

import ru.practicum.explorewithme.common.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.common.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.common.dto.request.ParticipationRequestDto;

import java.util.List;

public interface PrivateRequestService {
    ParticipationRequestDto save(long userId, long eventId);

    ParticipationRequestDto cancel(long userId, long requestId);

    List<ParticipationRequestDto> getUserRequests(long userId);

    List<ParticipationRequestDto> getEventRequests(long userId, long eventId);

    EventRequestStatusUpdateResult updateEventRequests(long userId,
                                                       long eventId,
                                                       EventRequestStatusUpdateRequest updateRequest);
}
