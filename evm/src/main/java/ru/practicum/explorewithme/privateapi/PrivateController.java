package ru.practicum.explorewithme.privateapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.common.dto.event.*;
import ru.practicum.explorewithme.common.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.common.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.common.dto.request.ParticipationRequestDto;
import ru.practicum.explorewithme.privateapi.service.event.PrivateEventService;
import ru.practicum.explorewithme.privateapi.service.request.PrivateRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateController {
    private final PrivateEventService eventService;
    private final PrivateRequestService requestService;

    @PostMapping("events")
    public ResponseEntity<EventFullDto> saveNewEvent(@PathVariable long userId,
                                                     @Valid @RequestBody NewEventDto eventDto) {
        log.info("Creating event: userId={}, eventDto={}", userId, eventDto);
        return new ResponseEntity<>(eventService.save(userId, eventDto), HttpStatus.CREATED);
    }

    @GetMapping("events/{eventId}")
    public ResponseEntity<EventFullDto> getEvent(@PathVariable long userId,
                                                 @PathVariable long eventId) {
        log.info("Get event: userId={}, eventId={}", userId, eventId);
        return new ResponseEntity<>(eventService.get(userId, eventId), HttpStatus.OK);
    }

    @GetMapping("events")
    public ResponseEntity<List<EventShortDto>> getEvents(@PathVariable long userId,
                                                         @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                         @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Get events: userId={}, from={}, size={}", userId, from, size);
        return new ResponseEntity<>(eventService.getAll(userId, from, size), HttpStatus.OK);
    }

    @PatchMapping("events/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(@PathVariable long userId,
                                                    @PathVariable long eventId,
                                                    @Valid @RequestBody UpdateEventRequest eventDto) {
        log.info("Updating event: userId={}, eventId={}, eventDto={}", userId, eventId, eventDto);
        return new ResponseEntity<>(eventService.update(userId, eventId, eventDto), HttpStatus.OK);
    }

    @PostMapping("requests")
    public ResponseEntity<ParticipationRequestDto> saveNewRequest(@PathVariable long userId,
                                                                  @RequestParam long eventId) {
        log.info("Creating request: userId={}, eventId={}", userId, eventId);
        return new ResponseEntity<>(requestService.save(userId, eventId), HttpStatus.CREATED);
    }

    @PatchMapping("requests/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(@PathVariable long userId,
                                                                 @PathVariable long requestId) {
        log.info("Cancel request: userId={}, requestId={}", userId, requestId);
        return new ResponseEntity<>(requestService.cancel(userId, requestId), HttpStatus.OK);
    }

    @GetMapping("requests")
    public ResponseEntity<List<ParticipationRequestDto>> getUserRequests(@PathVariable long userId) {
        log.info("Get all user requests: userId={}", userId);
        return new ResponseEntity<>(requestService.getUserRequests(userId), HttpStatus.OK);
    }

    @GetMapping("events/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getEventRequests(@PathVariable long userId,
                                                                          @PathVariable long eventId) {
        log.info("Get requests on event: userId={}, eventId={}", userId, eventId);
        return new ResponseEntity<>(requestService.getEventRequests(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping("events/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> updateEventRequests(
            @PathVariable long userId,
            @PathVariable long eventId,
            @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        log.info("Updating event requests status: userId={}, eventId={}, updateRequest={}",
                userId, eventId, updateRequest
        );
        return new ResponseEntity<>(requestService.updateEventRequests(userId, eventId, updateRequest), HttpStatus.OK);
    }
}
