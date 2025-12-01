package com.example.taskmanager.service;

import com.example.taskmanager.dto.CreateTaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.dto.TaskStatisticsResponse;
import com.example.taskmanager.dto.UpdateTaskRequest;
import com.example.taskmanager.entity.TaskStatus;

import java.util.List;
import java.util.UUID;

public interface TaskService {

    TaskResponse createTask(CreateTaskRequest request);

    TaskResponse updateTask(UUID id, UpdateTaskRequest request);

    void deleteTask(UUID id);

    TaskResponse getTask(UUID id);

    List<TaskResponse> getTasks(TaskStatus status, UUID categoryId, String sortBy, String query);


    TaskResponse updateStatus(UUID id, TaskStatus status);

    TaskStatisticsResponse getStatistics();
}
