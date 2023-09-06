package ru.practicum.explorewithme.privateapi.subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.common.dto.event.EventShortDto;
import ru.practicum.explorewithme.common.dto.user.UserDto;
import ru.practicum.explorewithme.common.dto.user.UserShortDto;
import ru.practicum.explorewithme.common.mapper.EventMapper;
import ru.practicum.explorewithme.common.mapper.UserMapper;
import ru.practicum.explorewithme.common.model.User;
import ru.practicum.explorewithme.common.repository.EventRepository;
import ru.practicum.explorewithme.common.repository.UserRepository;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.exception.ValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PrivateSubscriptionServiceImpl implements PrivateSubscriptionService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final UserMapper userMapper;
    private final EventMapper eventMapper;

    @Transactional
    @Override
    public UserDto subscribe(long userId, long followingId) {
        User follower = findUserOrThrowException(userId);
        User following = findUserOrThrowException(followingId);

        if (!follower.getSubscriptions().add(following)) {
            throw new ValidationException(
                    String.format("User with id=%d is already in subscriptions", followingId)
            );
        }
        User savedSubscriber = userRepository.save(follower);
        log.info("UserId={} subscribed to userId={}", userId, followingId);
        return userMapper.toDto(savedSubscriber);
    }

    @Transactional
    @Override
    public void unsubscribe(long userId, long followingId) {
        User follower = findUserOrThrowException(userId);
        User following = findUserOrThrowException(followingId);

        if (!follower.getSubscriptions().remove(following)) {
            throw new ValidationException(
                    String.format("There is no user with id=%d in subscriptions", followingId)
            );
        }

        userRepository.save(follower);
        log.info("UserId={} unsubscribed from userId={}", userId, followingId);
    }

    @Override
    public List<UserShortDto> getSubscriptions(long userId, int from, int size) {
        PageRequest pageRequest = makePage(from, size);
        return userRepository.findSubscriptions(userId, pageRequest).stream()
                .map(userMapper::toDtoShort)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserShortDto> getFollowers(long userId, int from, int size) {
        PageRequest pageRequest = makePage(from, size);
        return userRepository.findFollowers(userId, pageRequest).stream()
                .map(userMapper::toDtoShort)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getEventsByFollowings(long userId, int from, int size) {
        User user = findUserOrThrowException(userId);
        if (user.getSubscriptions() == null) {
            return List.of();
        }

        PageRequest pageRequest = makePage(from, size);
        return eventRepository.findRelevantEventsByFollowings(user.getSubscriptions(), pageRequest).stream()
                .map(eventMapper::toShortDto)
                .collect(Collectors.toList());
    }

    private User findUserOrThrowException(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id=%d was not found", userId)));
    }

    private PageRequest makePage(int from, int size) {
        return PageRequest.of(
                from / size,
                size,
                Sort.by(Sort.Direction.ASC, "id")
        );
    }
}
