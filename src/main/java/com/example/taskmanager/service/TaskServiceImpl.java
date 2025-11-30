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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final TaskMapper taskMapper;

    // ✅ ЯВНЫЙ КОНСТРУКТОР, без Lombok
    public TaskServiceImpl(TaskRepository taskRepository,
                           CategoryRepository categoryRepository,
                           TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.categoryRepository = categoryRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    public TaskResponse createTask(CreateTaskRequest request) {
        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(TaskStatus.TODO);

        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found: " + request.categoryId()));
            task.setCategory(category);
        }

        Task saved = taskRepository.save(task);
        return taskMapper.toDto(saved);
    }

    @Override
    public TaskResponse updateTask(UUID id, UpdateTaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found: " + id));

        task.setTitle(request.title());
        task.setDescription(request.description());

        if (request.status() != null) {
            task.setStatus(request.status());
        }

        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found: " + request.categoryId()));
            task.setCategory(category);
        } else {
            task.setCategory(null);
        }

        Task updated = taskRepository.save(task);
        return taskMapper.toDto(updated);
    }

    @Override
    public void deleteTask(UUID id) {
        if (!taskRepository.existsById(id)) {
            throw new NotFoundException("Task not found: " + id);
        }
        taskRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTask(UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found: " + id));
        return taskMapper.toDto(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasks(TaskStatus status, UUID categoryId, String sortBy) {
        List<Task> tasks;

        if (status != null && categoryId != null) {
            tasks = taskRepository.findByStatusAndCategory_Id(status, categoryId);
        } else if (status != null) {
            tasks = taskRepository.findByStatus(status);
        } else if (categoryId != null) {
            tasks = taskRepository.findByCategory_Id(categoryId);
        } else {
            tasks = taskRepository.findAll();
        }

        if (sortBy != null && sortBy.equals("createdAt")) {
            tasks = tasks.stream()
                    .sorted(Comparator.comparing(Task::getCreatedAt))
                    .toList();
        }

        return tasks.stream()
                .map(taskMapper::toDto)
                .toList();
    }

    @Override
    public TaskResponse updateStatus(UUID id, TaskStatus status) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found: " + id));

        task.setStatus(status);
        Task updated = taskRepository.save(task);
        return taskMapper.toDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskStatisticsResponse getStatistics() {
        List<Task> tasks = taskRepository.findAll();

        long total = tasks.size();
        long todo = tasks.stream().filter(t -> t.getStatus() == TaskStatus.TODO).count();
        long inProgress = tasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count();
        long done = tasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count();

        return new TaskStatisticsResponse(total, todo, inProgress, done);
    }
}
