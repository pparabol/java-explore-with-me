package ru.practicum.explorewithme.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.common.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
