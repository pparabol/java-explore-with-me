package ru.practicum.explorewithme.common.dto.event;

import lombok.Builder;
import lombok.Data;
import ru.practicum.explorewithme.common.dto.category.CategoryDto;
import ru.practicum.explorewithme.common.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Data
@Builder
public class EventShortDto {
    private String annotation;
    private CategoryDto category;
    private int confirmedRequests;
    private LocalDateTime eventDate;
    private Long id;
    private UserShortDto initiator;
    private boolean paid;
    private String title;
    private long views;
}
