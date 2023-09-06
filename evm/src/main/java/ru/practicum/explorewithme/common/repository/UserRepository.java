package ru.practicum.explorewithme.common.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.common.model.User;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByIdIn(List<Long> ids, Pageable pageable);

    @Query("SELECT subscriptions " +
            "FROM User u " +
            "WHERE u.id = :userId")
    List<User> findSubscriptions(Long userId, Pageable pageable);

    @Query("FROM User u " +
            "INNER JOIN u.subscriptions sub " +
            "WHERE sub.id = :userId")
    List<User> findFollowers(Long userId, Pageable pageable);
}
