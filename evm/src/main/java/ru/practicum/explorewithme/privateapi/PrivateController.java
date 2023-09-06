package ru.practicum.explorewithme.privateapi;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.common.dto.event.*;
import ru.practicum.explorewithme.common.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.common.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.common.dto.request.ParticipationRequestDto;
import ru.practicum.explorewithme.common.dto.user.UserDto;
import ru.practicum.explorewithme.common.dto.user.UserShortDto;
import ru.practicum.explorewithme.privateapi.service.event.PrivateEventService;
import ru.practicum.explorewithme.privateapi.service.request.PrivateRequestService;
import ru.practicum.explorewithme.privateapi.subscription.PrivateSubscriptionService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor
@Validated
public class PrivateController {
    private final PrivateEventService eventService;
    private final PrivateRequestService requestService;
    private final PrivateSubscriptionService subscriptionService;

    @PostMapping("events")
    public ResponseEntity<EventFullDto> saveNewEvent(@PathVariable long userId,
                                                     @Valid @RequestBody NewEventDto eventDto) {
        return new ResponseEntity<>(eventService.save(userId, eventDto), HttpStatus.CREATED);
    }

    @GetMapping("events/{eventId}")
    public ResponseEntity<EventFullDto> getEvent(@PathVariable long userId,
                                                 @PathVariable long eventId) {
        return new ResponseEntity<>(eventService.get(userId, eventId), HttpStatus.OK);
    }

    @GetMapping("events")
    public ResponseEntity<List<EventShortDto>> getEvents(@PathVariable long userId,
                                                         @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                         @RequestParam(defaultValue = "10") @Positive int size) {
        return new ResponseEntity<>(eventService.getAll(userId, from, size), HttpStatus.OK);
    }

    @PatchMapping("events/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(@PathVariable long userId,
                                                    @PathVariable long eventId,
                                                    @Valid @RequestBody UpdateEventRequest eventDto) {
        return new ResponseEntity<>(eventService.update(userId, eventId, eventDto), HttpStatus.OK);
    }

    @PostMapping("requests")
    public ResponseEntity<ParticipationRequestDto> saveNewRequest(@PathVariable long userId,
                                                                  @RequestParam long eventId) {
        return new ResponseEntity<>(requestService.save(userId, eventId), HttpStatus.CREATED);
    }

    @PatchMapping("requests/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(@PathVariable long userId,
                                                                 @PathVariable long requestId) {
        return new ResponseEntity<>(requestService.cancel(userId, requestId), HttpStatus.OK);
    }

    @GetMapping("requests")
    public ResponseEntity<List<ParticipationRequestDto>> getUserRequests(@PathVariable long userId) {
        return new ResponseEntity<>(requestService.getUserRequests(userId), HttpStatus.OK);
    }

    @GetMapping("events/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getEventRequests(@PathVariable long userId,
                                                                          @PathVariable long eventId) {
        return new ResponseEntity<>(requestService.getEventRequests(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping("events/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> updateEventRequests(
            @PathVariable long userId,
            @PathVariable long eventId,
            @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        return new ResponseEntity<>(requestService.updateEventRequests(userId, eventId, updateRequest), HttpStatus.OK);
    }

    @PostMapping("subscriptions")
    public ResponseEntity<UserDto> subscribe(@PathVariable long userId,
                                             @RequestParam @Positive long id) {
        if (userId == id) {
            throw new IllegalArgumentException("Identifiers are equal: cannot subscribe to yourself");
        }
        return new ResponseEntity<>(subscriptionService.subscribe(userId, id), HttpStatus.CREATED);
    }

    @DeleteMapping("subscriptions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsubscribe(@PathVariable long userId, @RequestParam @Positive long id) {
        if (userId == id) {
            throw new IllegalArgumentException("Identifiers are equal: cannot unsubscribe from yourself");
        }
        subscriptionService.unsubscribe(userId, id);
    }

    @GetMapping("subscriptions")
    public ResponseEntity<List<UserShortDto>> getUserSubscriptions(
            @PathVariable long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        return new ResponseEntity<>(subscriptionService.getSubscriptions(userId, from, size), HttpStatus.OK);
    }

    @GetMapping("followers")
    public ResponseEntity<List<UserShortDto>> getUserFollowers(
            @PathVariable long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        return new ResponseEntity<>(subscriptionService.getFollowers(userId, from, size), HttpStatus.OK);
    }

    @GetMapping("subscriptions/events")
    public ResponseEntity<List<EventShortDto>> getEventsByFollowings(
            @PathVariable long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        return new ResponseEntity<>(subscriptionService.getEventsByFollowings(userId, from, size), HttpStatus.OK);
    }
}
