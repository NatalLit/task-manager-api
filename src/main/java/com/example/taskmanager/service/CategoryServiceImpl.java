package com.example.taskmanager.service;

import com.example.taskmanager.dto.CategoryResponse;
import com.example.taskmanager.dto.CreateCategoryRequest;
import com.example.taskmanager.entity.Category;
import com.example.taskmanager.exception.BadRequestException;
import com.example.taskmanager.exception.NotFoundException;
import com.example.taskmanager.mapper.CategoryMapper;
import com.example.taskmanager.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    // ✅ ЯВНЫЙ КОНСТРУКТОР
    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.name())) {
            throw new BadRequestException("Category with name '" + request.name() + "' already exists");
        }

        Category category = new Category();
        category.setName(request.name());
        category.setDescription(request.description());

        Category saved = categoryRepository.save(category);
        return categoryMapper.toDto(saved);
    }

    @Override
    public CategoryResponse updateCategory(UUID id, CreateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found: " + id));

        if (!category.getName().equalsIgnoreCase(request.name())
                && categoryRepository.existsByNameIgnoreCase(request.name())) {
            throw new BadRequestException("Category with name '" + request.name() + "' already exists");
        }

        category.setName(request.name());
        category.setDescription(request.description());

        Category updated = categoryRepository.save(category);
        return categoryMapper.toDto(updated);
    }

    @Override
    public void deleteCategory(UUID id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("Category not found: " + id);
        }
        categoryRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategory(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found: " + id));

        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toDto)
                .toList();
    }
}
