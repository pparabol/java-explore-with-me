package ru.practicum.explorewithme.publicapi.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.common.dto.category.CategoryDto;
import ru.practicum.explorewithme.common.exception.NotFoundException;
import ru.practicum.explorewithme.common.model.Category;
import ru.practicum.explorewithme.common.repository.CategoryRepository;
import ru.practicum.explorewithme.mapper.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicCategoryServiceImpl implements PublicCategoryService {
    private final CategoryRepository categoryRepository;
    private final Mapper<Category, CategoryDto> categoryMapper;

    @Override
    public List<CategoryDto> getAll(int from, int size) {
        PageRequest pageRequest = PageRequest.of(
                from / size,
                size,
                Sort.by(Sort.Direction.ASC, "id")
        );
        return categoryRepository.findAll(pageRequest).toList().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto get(int catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException(String.format("Category with id=%d was not found", catId)));
        return categoryMapper.toDto(category);
    }
}
