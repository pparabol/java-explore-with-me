package ru.practicum.explorewithme.common.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.common.model.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Integer> {
    @Query("FROM Compilation c " +
            "WHERE (COALESCE(:pinned, null) IS NULL OR c.pinned = :pinned)")
    List<Compilation> findCompilations(Boolean pinned, Pageable pageable);
}
