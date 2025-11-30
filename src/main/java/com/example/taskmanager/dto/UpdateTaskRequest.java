package com.example.taskmanager.dto;

import com.example.taskmanager.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UpdateTaskRequest(
        @NotBlank
        String title,
        String description,
        UUID categoryId,
        TaskStatus status
) {
}

