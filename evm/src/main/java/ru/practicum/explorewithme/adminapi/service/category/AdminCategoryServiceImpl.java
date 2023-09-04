package ru.practicum.explorewithme.adminapi.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.common.dto.category.CategoryDto;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.common.model.Category;
import ru.practicum.explorewithme.common.repository.CategoryRepository;
import ru.practicum.explorewithme.mapper.Mapper;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCategoryServiceImpl implements AdminCategoryService {
    private final CategoryRepository categoryRepository;
    private final Mapper<Category, CategoryDto> categoryMapper;

    @Transactional
    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        Category category = categoryRepository.save(categoryMapper.toEntity(categoryDto));
        return categoryMapper.toDto(category);
    }

    @Transactional
    @Override
    public void delete(int categoryId) {
        findCategoryOrThrowException(categoryId);
        categoryRepository.deleteById(categoryId);
    }

    @Transactional
    @Override
    public CategoryDto update(CategoryDto categoryDto) {
        Category category = findCategoryOrThrowException(categoryDto.getId());
        category.setName(categoryDto.getName());
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    private Category findCategoryOrThrowException(int catId) {
        return categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException(String.format("Category with id=%d was not found", catId)));
    }
}
