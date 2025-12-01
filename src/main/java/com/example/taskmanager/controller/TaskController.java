package com.example.taskmanager.controller;

import com.example.taskmanager.dto.CreateTaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.dto.TaskStatisticsResponse;
import com.example.taskmanager.dto.UpdateTaskRequest;
import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    // Обычный конструктор без Lombok
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public TaskResponse create(@Valid @RequestBody CreateTaskRequest request) {
        return taskService.createTask(request);
    }

    @GetMapping
    public List<TaskResponse> getAll(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false) String query
    ) {
        return taskService.getTasks(status, categoryId, sortBy, query);
    }

    @GetMapping("/{id}")
    public TaskResponse getById(@PathVariable UUID id) {
        return taskService.getTask(id);
    }

    @PutMapping("/{id}")
    public TaskResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskRequest request
    ) {
        return taskService.updateTask(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        taskService.deleteTask(id);
    }

    @PatchMapping("/{id}/status")
    public TaskResponse updateStatus(
            @PathVariable UUID id,
            @RequestParam TaskStatus status
    ) {
        return taskService.updateStatus(id, status);
    }

    @GetMapping("/statistics")
    public TaskStatisticsResponse getStatistics() {
        return taskService.getStatistics();
    }
}
