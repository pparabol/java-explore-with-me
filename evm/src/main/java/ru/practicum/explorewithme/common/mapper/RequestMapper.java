package ru.practicum.explorewithme.common.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.common.dto.request.ParticipationRequestDto;
import ru.practicum.explorewithme.common.dto.request.RequestStatus;
import ru.practicum.explorewithme.common.model.Event;
import ru.practicum.explorewithme.common.model.ParticipationRequest;
import ru.practicum.explorewithme.common.model.User;

import java.time.LocalDateTime;

@Component
public class RequestMapper {
    public ParticipationRequest toEntity(RequestStatus status, User user, Event event) {
        return ParticipationRequest.builder()
                .requester(user)
                .event(event)
                .status(status)
                .created(LocalDateTime.now())
                .build();
    }

    public ParticipationRequestDto toDto(ParticipationRequest entity) {
        return ParticipationRequestDto.builder()
                .created(entity.getCreated())
                .event(entity.getEvent().getId())
                .id(entity.getId())
                .requester(entity.getRequester().getId())
                .status(entity.getStatus())
                .build();
    }
}
