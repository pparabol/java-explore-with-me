package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.EndpointHitDto;
import ru.practicum.explorewithme.EventView;
import ru.practicum.explorewithme.ViewStatsDto;
import ru.practicum.explorewithme.mapper.Mapper;
import ru.practicum.explorewithme.model.EndpointHit;
import ru.practicum.explorewithme.model.ViewStats;
import ru.practicum.explorewithme.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;
    private final Mapper<EndpointHit, EndpointHitDto> hitMapper;
    private final Mapper<ViewStats, ViewStatsDto> statsMapper;

    @Override
    public void saveHit(EndpointHitDto endpointHitDto) {
        EndpointHit hit = statsRepository.save(hitMapper.toEntity(endpointHitDto));
        log.info("Saved endpoint hit: {}", hit);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        List<ViewStats> stats;
        if (unique) {
            stats = statsRepository.findUniqueStatistics(start, end, uris);
        } else {
            stats = statsRepository.findStatistics(start, end, uris);
        }
        return stats.stream()
                .map(statsMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventView getEventView(String uri, String ip) {
        return statsRepository.findByUriAndIp(uri, ip).orElse(null);
    }
}
