package com.bsdev.crud_webapp.service;

import com.bsdev.crud_webapp.dto.TaskRequest;
import com.bsdev.crud_webapp.dto.TaskResponse;
import com.bsdev.crud_webapp.entity.Task;
import com.bsdev.crud_webapp.exception.TaskNotFoundException;
import com.bsdev.crud_webapp.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    @Transactional
    public TaskResponse createTask(TaskRequest taskRequest) {
        Task task = Task.builder()
                .title(taskRequest.title())
                .description(taskRequest.description())
                .userId(taskRequest.userId())
                .build();
        Task result = taskRepository.save(task);
        return toResponse(result);
    }

    private Task getTaskByIdOrThrow(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    public TaskResponse findTaskByIdOrThrow(Long id) {
        Task task = getTaskByIdOrThrow(id);
        return toResponse(task);
    }

    @Transactional
    public void updateTask(Long id, TaskRequest taskRequest) {
        Task task = getTaskByIdOrThrow(id);
        task.setTitle(taskRequest.title());
        task.setDescription(taskRequest.description());
        task.setUserId(taskRequest.userId());
        taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(Long id) {
        findTaskByIdOrThrow(id);
        taskRepository.deleteById(id);
    }

    public List<TaskResponse> findAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getUserId());
    }
}
