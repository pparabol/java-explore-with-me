package ru.practicum.explorewithme.common.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.common.dto.user.UserDto;
import ru.practicum.explorewithme.common.model.User;
import ru.practicum.explorewithme.mapper.Mapper;

@Component
public class UserMapper implements Mapper<User, UserDto> {
    @Override
    public User toEntity(UserDto dto) {
        return User.builder()
                .id(dto.getId())
                .email(dto.getEmail())
                .name(dto.getName())
                .build();
    }

    @Override
    public UserDto toDto(User entity) {
        return UserDto.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .name(entity.getName())
                .build();
    }
}
