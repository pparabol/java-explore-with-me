package ru.practicum.explorewithme.common.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.common.dto.category.CategoryDto;
import ru.practicum.explorewithme.common.model.Category;
import ru.practicum.explorewithme.mapper.Mapper;

@Component
public class CategoryMapper implements Mapper<Category, CategoryDto> {
    @Override
    public Category toEntity(CategoryDto dto) {
        return Category.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }

    @Override
    public CategoryDto toDto(Category entity) {
        return CategoryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}
