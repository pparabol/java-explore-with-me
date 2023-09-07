package ru.practicum.explorewithme.privateapi.subscription;

import ru.practicum.explorewithme.common.dto.event.EventShortDto;
import ru.practicum.explorewithme.common.dto.user.UserDto;
import ru.practicum.explorewithme.common.dto.user.UserShortDto;

import java.util.List;

public interface PrivateSubscriptionService {
    UserDto subscribe(long userId, long followingId);

    void unsubscribe(long userId, long followingId);

    List<UserShortDto> getSubscriptions(long userId, int from, int size);

    List<UserShortDto> getFollowers(long userId, int from, int size);

    List<EventShortDto> getEventsByFollowings(long userId, int from, int size);
}
