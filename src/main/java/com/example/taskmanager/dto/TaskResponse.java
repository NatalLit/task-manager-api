package com.example.taskmanager.dto;

import com.example.taskmanager.entity.TaskStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        String title,
        String description,
        TaskStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        UUID categoryId
) {
}

