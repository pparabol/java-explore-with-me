package ru.practicum.explorewithme.privateapi.service.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.common.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.common.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.common.dto.event.EventState;
import ru.practicum.explorewithme.common.dto.request.ParticipationRequestDto;
import ru.practicum.explorewithme.common.dto.request.RequestStatus;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.exception.ValidationException;
import ru.practicum.explorewithme.common.mapper.RequestMapper;
import ru.practicum.explorewithme.common.model.Event;
import ru.practicum.explorewithme.common.model.ParticipationRequest;
import ru.practicum.explorewithme.common.model.User;
import ru.practicum.explorewithme.common.repository.EventRepository;
import ru.practicum.explorewithme.common.repository.RequestRepository;
import ru.practicum.explorewithme.common.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PrivateRequestServiceImpl implements PrivateRequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;

    @Transactional
    @Override
    public ParticipationRequestDto save(long userId, long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id=%d was not found", userId)));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException((String.format("Event with id=%d was not found", eventId))));

        if (event.getInitiator() == user) {
            throw new ValidationException("Initiator cannot participate in their own event");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException(String.format("Cannon participate in the event with id=%d " +
                    "because it's not in the right state: %s", eventId, event.getState()));
        }
        if (event.getParticipantLimit() != 0 &&
                event.getConfirmedRequests() == event.getParticipantLimit()) {
            throw new ValidationException(String.format("Cannon participate in the event with id=%d " +
                    "because the participant limit has been reached", eventId));
        }

        RequestStatus status;
        if (event.getRequestModeration() && event.getParticipantLimit() > 0) {
            status = RequestStatus.PENDING;
        } else {
            status = RequestStatus.CONFIRMED;
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }

        ParticipationRequest request = requestMapper.toEntity(status, user, event);
        log.info("Saved new participation request: {}", request);
        return requestMapper.toDto(requestRepository.save(request));
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancel(long userId, long requestId) {
        ParticipationRequest request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Request with id=%d was not found", requestId)));

        request.setStatus(RequestStatus.CANCELED);
        log.info("Participation request has been canceled: {}", request);
        return requestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(long userId) {
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParticipationRequestDto> getEventRequests(long userId, long eventId) {
        findEventOrThrowException(eventId, userId);
        return requestRepository.findAllByEventId(eventId).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateEventRequests(long userId,
                                                              long eventId,
                                                              EventRequestStatusUpdateRequest updateRequest) {
        Event event = findEventOrThrowException(eventId, userId);
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            return EventRequestStatusUpdateResult.builder()
                    .confirmedRequests(Set.of())
                    .rejectedRequests(Set.of())
                    .build();
        }

        List<ParticipationRequest> requests = requestRepository.findAllByIdInAndEventId(
                updateRequest.getRequestIds(), eventId);
        if (!requests.stream()
                .map(ParticipationRequest::getStatus)
                .allMatch(RequestStatus.PENDING::equals)) {
            throw new ValidationException("Request status must be PENDING");
        }

        Set<ParticipationRequest> confirmedRequests = new HashSet<>();
        Set<ParticipationRequest> rejectedRequests = new HashSet<>();

        if (updateRequest.getStatus().equals(RequestStatus.CONFIRMED)) {
            if (event.getParticipantLimit() == event.getConfirmedRequests()) {
                throw new ValidationException("The participant limit on event " +
                        "with id=" + eventId + " has been reached");
            }
            int limit = event.getParticipantLimit() - event.getConfirmedRequests();
            if (requests.size() > limit) {
                rejectedRequests = requests.subList(limit, requests.size()).stream()
                        .map(r -> changeStatus(r, RequestStatus.REJECTED))
                        .map(requestRepository::save)
                        .collect(Collectors.toSet());

                requests = requests.subList(0, limit);
            }
            confirmedRequests = requests.stream()
                    .map(r -> changeStatus(r, RequestStatus.CONFIRMED))
                    .map(requestRepository::save)
                    .collect(Collectors.toSet());

            event.setConfirmedRequests(event.getConfirmedRequests() + requests.size());
            eventRepository.save(event);
        }
        if (updateRequest.getStatus().equals(RequestStatus.REJECTED)) {
            rejectedRequests = requests.stream()
                    .map(r -> changeStatus(r, RequestStatus.REJECTED))
                    .map(requestRepository::save)
                    .collect(Collectors.toSet());
        }
        log.info("Requests on eventId={} has been {} by userId={}: requests={}",
                eventId, updateRequest.getStatus(), userId, requests);

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests.stream().map(requestMapper::toDto).collect(Collectors.toSet()))
                .rejectedRequests(rejectedRequests.stream().map(requestMapper::toDto).collect(Collectors.toSet()))
                .build();
    }

    private Event findEventOrThrowException(long eventId, long userId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() ->
                        new NotFoundException((String.format("Event with id=%d was not found", eventId))));
    }

    private ParticipationRequest changeStatus(ParticipationRequest request, RequestStatus status) {
        request.setStatus(status);
        return request;
    }
}
