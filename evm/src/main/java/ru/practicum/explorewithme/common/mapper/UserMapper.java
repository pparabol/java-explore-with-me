package ru.practicum.explorewithme.common.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.common.dto.user.UserDto;
import ru.practicum.explorewithme.common.dto.user.UserShortDto;
import ru.practicum.explorewithme.common.model.User;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public User toEntity(UserDto dto) {
        return User.builder()
                .id(dto.getId())
                .email(dto.getEmail())
                .name(dto.getName())
                .build();
    }

    public UserDto toDto(User entity) {
        return UserDto.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .name(entity.getName())
                .subscriptions(entity.getSubscriptions() == null ? Set.of() :
                        entity.getSubscriptions().stream()
                                .map(User::getId)
                                .collect(Collectors.toSet()))
                .build();
    }

    public UserShortDto toDtoShort(User entity) {
        return new UserShortDto(entity.getId(), entity.getName());
    }
}
