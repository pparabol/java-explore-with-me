package ru.practicum.explorewithme.adminapi.service.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AdminCategoryServiceImpl implements AdminCategoryService {
    private final CategoryRepository categoryRepository;
    private final Mapper<Category, CategoryDto> categoryMapper;

    @Transactional
    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        Category category = categoryRepository.save(categoryMapper.toEntity(categoryDto));
        log.info("Saved category: {}", category);
        return categoryMapper.toDto(category);
    }

    @Transactional
    @Override
    public void delete(int categoryId) {
        findCategoryOrThrowException(categoryId);
        categoryRepository.deleteById(categoryId);
        log.info("Category with id={} has been deleted", categoryId);
    }

    @Transactional
    @Override
    public CategoryDto update(CategoryDto categoryDto) {
        Category category = findCategoryOrThrowException(categoryDto.getId());
        category.setName(categoryDto.getName());
        Category updated = categoryRepository.save(category);
        log.info("Updated category: {}", updated);
        return categoryMapper.toDto(updated);
    }

    private Category findCategoryOrThrowException(int catId) {
        return categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException(String.format("Category with id=%d was not found", catId)));
    }
}
