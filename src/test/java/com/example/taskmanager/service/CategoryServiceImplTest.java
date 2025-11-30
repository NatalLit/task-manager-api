package com.example.taskmanager.service;

import com.example.taskmanager.dto.CategoryResponse;
import com.example.taskmanager.dto.CreateCategoryRequest;
import com.example.taskmanager.entity.Category;
import com.example.taskmanager.exception.BadRequestException;
import com.example.taskmanager.exception.NotFoundException;
import com.example.taskmanager.mapper.CategoryMapper;
import com.example.taskmanager.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void createCategory_success() {
        CreateCategoryRequest request = new CreateCategoryRequest("Work", "Work tasks");

        when(categoryRepository.existsByNameIgnoreCase("Work")).thenReturn(false);

        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setName("Work");
        category.setDescription("Work tasks");

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryResponse response = new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription()
        );

        when(categoryMapper.toDto(category)).thenReturn(response);

        CategoryResponse result = categoryService.createCategory(request);

        assertEquals("Work", result.name());
        assertEquals("Work tasks", result.description());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategory_whenNameExists_throwsBadRequest() {
        CreateCategoryRequest request = new CreateCategoryRequest("Work", "desc");

        when(categoryRepository.existsByNameIgnoreCase("Work")).thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> categoryService.createCategory(request));

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void getCategory_whenExists_returnsResponse() {
        UUID id = UUID.randomUUID();

        Category category = new Category();
        category.setId(id);
        category.setName("Home");
        category.setDescription("Home tasks");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));

        CategoryResponse response = new CategoryResponse(
                id,
                "Home",
                "Home tasks"
        );

        when(categoryMapper.toDto(category)).thenReturn(response);

        CategoryResponse result = categoryService.getCategory(id);

        assertEquals(id, result.id());
        assertEquals("Home", result.name());
    }

    @Test
    void getCategory_whenNotFound_throwsNotFound() {
        UUID id = UUID.randomUUID();

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> categoryService.getCategory(id));
    }

    @Test
    void getAllCategories_returnsList() {
        Category c1 = new Category();
        c1.setId(UUID.randomUUID());
        c1.setName("Work");

        Category c2 = new Category();
        c2.setId(UUID.randomUUID());
        c2.setName("Home");

        when(categoryRepository.findAll()).thenReturn(List.of(c1, c2));

        when(categoryMapper.toDto(c1)).thenReturn(
                new CategoryResponse(c1.getId(), c1.getName(), c1.getDescription())
        );
        when(categoryMapper.toDto(c2)).thenReturn(
                new CategoryResponse(c2.getId(), c2.getName(), c2.getDescription())
        );

        List<CategoryResponse> result = categoryService.getAllCategories();

        assertEquals(2, result.size());
        verify(categoryRepository).findAll();
    }
}
