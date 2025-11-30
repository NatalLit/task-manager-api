package com.example.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequest(
        @NotBlank
        String name,
        String description
) {
}
