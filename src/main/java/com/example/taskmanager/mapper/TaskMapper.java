package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.entity.Task;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TaskMapper {

    public TaskResponse toDto(Task entity) {
        UUID categoryId = entity.getCategory() != null
                ? entity.getCategory().getId()
                : null;

        return new TaskResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                categoryId
        );
    }
}
