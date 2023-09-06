package ru.practicum.explorewithme.common.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.common.dto.event.EventState;
import ru.practicum.explorewithme.common.model.Event;
import ru.practicum.explorewithme.common.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByIdAndInitiatorId(Long id, Long userId);

    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    Set<Event> findAllByIdIn(Collection<Long> ids);

    @Query("FROM Event e " +
            "WHERE (COALESCE(:users, null) IS NULL OR e.initiator.id IN (:users)) " +
            "AND (COALESCE(:categories, null) IS NULL OR e.category.id IN (:categories)) " +
            "AND (COALESCE(:states, null) IS NULL OR e.state IN (:states)) " +
            "AND (COALESCE(:rangeStart, null) IS NULL OR e.eventDate >= CAST(:rangeStart as timestamp)) " +
            "AND (COALESCE(:rangeEnd, null) IS NULL OR e.eventDate <= CAST(:rangeEnd as timestamp))")
    List<Event> findEventsByCriteria(Set<Long> users,
                                     Set<Integer> categories,
                                     Set<EventState> states,
                                     LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd,
                                     Pageable pageable);

    Optional<Event> findByIdAndState(Long id, EventState state);

    @Query("FROM Event e " +
            "WHERE (:text IS NULL OR lower(e.annotation) LIKE lower(CONCAT('%', :text, '%')) " +
            "OR lower(e.description) LIKE lower(CONCAT('%', :text, '%')))" +
            "AND (COALESCE(:categories, null) IS NULL OR e.category.id IN (:categories)) " +
            "AND (COALESCE(:paid, null) IS NULL OR e.paid = :paid) " +
            "AND (COALESCE(:rangeStart, null) IS NULL OR e.eventDate >= CAST(:rangeStart as timestamp)) " +
            "AND (COALESCE(:rangeEnd, null) IS NULL OR e.eventDate <= CAST(:rangeEnd as timestamp)) " +
            "AND e.state = 'PUBLISHED' " +
            "AND (:onlyAvailable = FALSE OR (e.participantLimit = 0 OR e.requestModeration = FALSE " +
            "OR e.participantLimit > e.confirmedRequests))")
    List<Event> findEventsByCriteria(String text,
                                     Set<Integer> categories,
                                     Boolean paid,
                                     LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd,
                                     Boolean onlyAvailable,
                                     Pageable pageable);

    @Query("FROM Event e " +
            "WHERE (:text IS NULL OR lower(e.annotation) LIKE lower(CONCAT('%', :text, '%')) " +
            "OR lower(e.description) LIKE lower(CONCAT('%', :text, '%')))" +
            "AND (COALESCE(:categories, null) IS NULL OR e.category.id IN (:categories)) " +
            "AND (COALESCE(:paid, null) IS NULL OR e.paid = :paid) " +
            "AND e.state = 'PUBLISHED' " +
            "AND (:onlyAvailable = FALSE OR (e.participantLimit = 0 OR e.requestModeration = FALSE " +
            "OR e.participantLimit > e.confirmedRequests)) " +
            "AND e.eventDate >= now()")
    List<Event> findFutureEventsByCriteria(String text,
                                           Set<Integer> categories,
                                           Boolean paid,
                                           Boolean onlyAvailable,
                                           Pageable pageable);

    @Query("FROM Event e " +
            "WHERE initiator IN (:initiators) " +
            "AND state = 'PUBLISHED' " +
            "AND (e.participantLimit = 0 OR e.requestModeration = FALSE " +
            "OR e.participantLimit > e.confirmedRequests)")
    List<Event> findRelevantEventsByFollowings(Collection<User> initiators, Pageable pageable);
}
