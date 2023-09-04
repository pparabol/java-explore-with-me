package ru.practicum.explorewithme.adminapi.service.user;

import ru.practicum.explorewithme.common.dto.user.UserDto;

import java.util.List;

public interface AdminUserService {
    UserDto save(UserDto userDto);

    void delete(long userId);

    List<UserDto> getUsers(List<Long> ids, int from, int size);
}
