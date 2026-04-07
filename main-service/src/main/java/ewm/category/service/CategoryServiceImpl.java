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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        log.info("Добавление новой категории: {}", newCategoryDto.name());
        Category category = categoryMapper.toEntity(newCategoryDto);

        try {
            categoryRepository.save(category);
            log.info("Добавлена категория с ID: {}", category.getId());
        } catch (DataIntegrityViolationException e) {
            log.warn("Уже существует категория с именем: {}", category.getName());
            throw new ConflictException("Категория с именем " + category.getName() + " уже существует");
        }

        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional
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
    @Transactional
    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto) {
        log.info("Обновление категории с id: {}, новое имя: {}", categoryId, categoryDto.name());

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                   log.warn("Категория с id {} не найдена", categoryId);
                   return new NotFoundException("Категория с id= " + categoryId + " не найдена");
                });

        category.setName(categoryDto.name());

        try {
            category = categoryRepository.save(category);
            log.info("Категория с id {} обновлена", categoryId);
        } catch (DataIntegrityViolationException  e) {
            log.warn("Категория с именем: {} Уже существует ", categoryDto.name());
            throw new ConflictException("Категория с именем " + categoryDto.name() + " уже существует");
        }

        return categoryMapper.toDto(category);
    }

    @Override
    public List<CategoryDto> getAllCategory(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());

        List<Category> categories = categoryRepository.findAll(pageable).getContent();

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
