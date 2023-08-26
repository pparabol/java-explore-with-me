package ru.practicum.explorewithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.model.EndpointHit;
import ru.practicum.explorewithme.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("SELECT new ru.practicum.explorewithme.model.ViewStats(eh.app, eh.uri, " +
            "COUNT(DISTINCT eh.ip) AS hits) " +
            "FROM EndpointHit eh " +
            "WHERE eh.timestamp BETWEEN :start AND :end " +
            "AND (COALESCE(:uris, null) IS NULL OR eh.uri IN (:uris))" +
            "GROUP BY eh.app, eh.uri " +
            "ORDER BY hits DESC")
    List<ViewStats> findUniqueStatistics(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.explorewithme.model.ViewStats(eh.app, eh.uri, " +
            "COUNT(eh.ip) AS hits) " +
            "FROM EndpointHit eh " +
            "WHERE eh.timestamp BETWEEN :start AND :end " +
            "AND (COALESCE(:uris, null) IS NULL OR eh.uri IN (:uris))" +
            "GROUP BY eh.app, eh.uri " +
            "ORDER BY hits DESC")
    List<ViewStats> findStatistics(LocalDateTime start, LocalDateTime end, List<String> uris);
}
