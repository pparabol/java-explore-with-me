package ru.practicum.explorewithme.adminapi.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.common.dto.user.UserDto;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.common.model.User;
import ru.practicum.explorewithme.common.repository.UserRepository;
import ru.practicum.explorewithme.mapper.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdminUserServiceImpl implements AdminUserService {
    private final UserRepository userRepository;
    private final Mapper<User, UserDto> userMapper;

    @Transactional
    @Override
    public UserDto save(UserDto userDto) {
        User user = userRepository.save(userMapper.toEntity(userDto));
        log.info("Saved user: {}", user);
        return userMapper.toDto(user);
    }

    @Transactional
    @Override
    public void delete(long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id=%d was not found", userId)));
        userRepository.deleteById(userId);
        log.info("User with id={} has been deleted", userId);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        PageRequest pageRequest = PageRequest.of(
                from / size,
                size,
                Sort.by(Sort.Direction.ASC, "id")
        );
        List<User> users;
        if (ids != null) {
            users = userRepository.findAllByIdIn(ids, pageRequest);
        } else {
            users = userRepository.findAll(pageRequest).toList();
        }
        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
}
