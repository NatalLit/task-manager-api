package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.CategoryResponse;
import com.example.taskmanager.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponse toDto(Category entity) {
        return new CategoryResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription()
        );
    }
}
