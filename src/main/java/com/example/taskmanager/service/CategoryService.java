package com.example.taskmanager.service;

import com.example.taskmanager.dto.CategoryResponse;
import com.example.taskmanager.dto.CreateCategoryRequest;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    CategoryResponse createCategory(CreateCategoryRequest request);

    CategoryResponse updateCategory(UUID id, CreateCategoryRequest request);

    void deleteCategory(UUID id);

    CategoryResponse getCategory(UUID id);

    List<CategoryResponse> getAllCategories();
}
