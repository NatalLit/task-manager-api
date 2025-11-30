package com.example.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CreateTaskRequest(
        @NotBlank
        String title,
        String description,
        UUID categoryId
) {
}
