package com.example.taskmanager.service;

import com.example.taskmanager.dto.CreateTaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.dto.TaskStatisticsResponse;
import com.example.taskmanager.dto.UpdateTaskRequest;
import com.example.taskmanager.entity.Category;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.exception.NotFoundException;
import com.example.taskmanager.mapper.TaskMapper;
import com.example.taskmanager.repository.CategoryRepository;
import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Test
    void createTask_withoutCategory_success() {
        CreateTaskRequest request = new CreateTaskRequest(
                "Test task",
                "desc",
                null
        );

        Task task = new Task();
        task.setId(UUID.randomUUID());
        task.setTitle("Test task");
        task.setDescription("desc");
        task.setStatus(TaskStatus.TODO);

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponse response = new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                null
        );

        when(taskMapper.toDto(task)).thenReturn(response);

        TaskResponse result = taskService.createTask(request);

        assertEquals("Test task", result.title());
        assertNull(result.categoryId());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void createTask_withCategory_success() {
        UUID categoryId = UUID.randomUUID();

        CreateTaskRequest request = new CreateTaskRequest(
                "Task with category",
                "desc",
                categoryId
        );

        Category category = new Category();
        category.setId(categoryId);
        category.setName("Work");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        Task task = new Task();
        task.setId(UUID.randomUUID());
        task.setTitle("Task with category");
        task.setDescription("desc");
        task.setStatus(TaskStatus.TODO);
        task.setCategory(category);

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponse response = new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                categoryId
        );

        when(taskMapper.toDto(task)).thenReturn(response);

        TaskResponse result = taskService.createTask(request);

        assertEquals(categoryId, result.categoryId());
        verify(categoryRepository).findById(categoryId);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void createTask_whenCategoryNotFound_throwsNotFound() {
        UUID categoryId = UUID.randomUUID();

        CreateTaskRequest request = new CreateTaskRequest(
                "Task",
                "desc",
                categoryId
        );

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> taskService.createTask(request));

        verify(taskRepository, never()).save(any());
    }

    @Test
    void getTask_whenExists_returnsResponse() {
        UUID id = UUID.randomUUID();

        Task task = new Task();
        task.setId(id);
        task.setTitle("Test");
        task.setDescription("desc");
        task.setStatus(TaskStatus.TODO);

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));

        TaskResponse response = new TaskResponse(
                id,
                "Test",
                "desc",
                TaskStatus.TODO,
                task.getCreatedAt(),
                task.getUpdatedAt(),
                null
        );

        when(taskMapper.toDto(task)).thenReturn(response);

        TaskResponse result = taskService.getTask(id);

        assertEquals(id, result.id());
        assertEquals("Test", result.title());
    }

    @Test
    void getTask_whenNotFound_throwsNotFound() {
        UUID id = UUID.randomUUID();

        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> taskService.getTask(id));
    }

    @Test
    void updateStatus_success() {
        UUID id = UUID.randomUUID();

        Task task = new Task();
        task.setId(id);
        task.setTitle("Test");
        task.setStatus(TaskStatus.TODO);

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));

        Task updated = new Task();
        updated.setId(id);
        updated.setTitle("Test");
        updated.setStatus(TaskStatus.DONE);

        when(taskRepository.save(task)).thenReturn(updated);

        TaskResponse response = new TaskResponse(
                id,
                "Test",
                null,
                TaskStatus.DONE,
                updated.getCreatedAt(),
                updated.getUpdatedAt(),
                null
        );

        when(taskMapper.toDto(updated)).thenReturn(response);

        TaskResponse result = taskService.updateStatus(id, TaskStatus.DONE);

        assertEquals(TaskStatus.DONE, result.status());
        verify(taskRepository).save(task);
    }

    @Test
    void getStatistics_countsByStatus() {
        Task t1 = new Task();
        t1.setId(UUID.randomUUID());
        t1.setStatus(TaskStatus.TODO);
        t1.setCreatedAt(LocalDateTime.now());

        Task t2 = new Task();
        t2.setId(UUID.randomUUID());
        t2.setStatus(TaskStatus.IN_PROGRESS);

        Task t3 = new Task();
        t3.setId(UUID.randomUUID());
        t3.setStatus(TaskStatus.DONE);

        when(taskRepository.findAll()).thenReturn(List.of(t1, t2, t3));

        TaskStatisticsResponse stats = taskService.getStatistics();

        assertEquals(3, stats.total());
        assertEquals(1, stats.todo());
        assertEquals(1, stats.inProgress());
        assertEquals(1, stats.done());
    }
}
