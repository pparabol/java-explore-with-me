package ru.practicum.explorewithme.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.common.model.ParticipationRequest;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    Optional<ParticipationRequest> findByIdAndRequesterId(Long id, Long userId);

    List<ParticipationRequest> findAllByRequesterId(Long userId);

    List<ParticipationRequest> findAllByEventId(Long eventId);

    List<ParticipationRequest> findAllByIdInAndEventId(Collection<Long> ids, Long eventId);
}
