package ru.practicum.explorewithme.adminapi.service.category;

import ru.practicum.explorewithme.common.dto.category.CategoryDto;

public interface AdminCategoryService {
    CategoryDto save(CategoryDto categoryDto);

    void delete(int categoryId);

    CategoryDto update(CategoryDto categoryDto);
}
