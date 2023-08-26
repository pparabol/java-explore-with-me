package ru.practicum.explorewithme.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.ViewStatsDto;
import ru.practicum.explorewithme.model.ViewStats;

@Component
public class ViewStatsMapper implements Mapper<ViewStats, ViewStatsDto> {
    @Override
    public ViewStats toEntity(ViewStatsDto dto) {
        return ViewStats.builder()
                .app(dto.getApp())
                .uri(dto.getUri())
                .hits(dto.getHits())
                .build();

    }

    @Override
    public ViewStatsDto toDto(ViewStats entity) {
        return ViewStatsDto.builder()
                .app(entity.getApp())
                .uri(entity.getUri())
                .hits(entity.getHits())
                .build();
    }
}
