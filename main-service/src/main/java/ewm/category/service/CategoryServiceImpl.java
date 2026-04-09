package ewm.category.service;

import ewm.category.dto.CategoryDto;
import ewm.category.dto.NewCategoryDto;
import ewm.category.mapper.CategoryMapper;
import ewm.category.model.Category;
import ewm.category.repository.CategoryRepository;
import ewm.exception.ConflictException;
import ewm.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor

public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        log.info("Добавление новой категории: {}", newCategoryDto.name());
        Category category = categoryMapper.toEntity(newCategoryDto);

        if (categoryRepository.existsByName(category.getName())) {
            log.warn("Уже существует категория с именем: {}", category.getName());
            throw new ConflictException("Категория с именем " + category.getName() + " уже существует");
        }

        category = categoryRepository.save(category);
        log.info("Добавлена категория с ID: {}", category.getId());

        return categoryMapper.toDto(category);
    }

    @Override
    public void deleteCategoryById(Long categoryId) {
        log.info("Удаление категории с id: {}", categoryId);

        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Категория с id= " + categoryId + " не найдена"));

        if (!categoryRepository.existsById(categoryId)) {
            throw new ConflictException("Категория с id= " + categoryId + " не найдена");
        }

        categoryRepository.deleteById(categoryId);
        log.info("Категория с id {} удалена", categoryId);
    }

    // имя категории должно быть уникальным
    @Override
    public CategoryDto updateCategory(Long categoryId, NewCategoryDto categoryDto) {
        log.info("Обновление категории с id: {}, новое имя: {}", categoryId, categoryDto.name());

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                   log.warn("Категория с id {} не найдена", categoryId);
                   return new NotFoundException("Категория с id= " + categoryId + " не найдена");
                });

        if (categoryRepository.existsByNameAndIdNot(categoryDto.name(), categoryId)) {
            log.warn("Уже существует категория с именем: {}", categoryDto.name());
            throw new ConflictException("Категория с именем " + categoryDto.name() + " уже существует");
        }

        category.setName(categoryDto.name());
        category = categoryRepository.save(category);
        log.info("Категория с id {} обновлена", categoryId);

        return categoryMapper.toDto(category);
    }

    @Override
    public List<CategoryDto> getAllCategory(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());

        List<Category> categories = categoryRepository.findAll(pageable).stream().toList();

        log.info("Получен список категорий: {}", categories);
        return categories.stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));

        log.info("Получена категория с id = {}", catId);

        return categoryMapper.toDto(category);
    }
}
