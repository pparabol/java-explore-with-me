package ru.practicum.explorewithme.publicapi.service.category;

import ru.practicum.explorewithme.common.dto.category.CategoryDto;

import java.util.List;

public interface PublicCategoryService {
    List<CategoryDto> getAll(int from, int size);

    CategoryDto get(int catId);
}
