package com.bsdev.crud_webapp.controller;

import com.bsdev.crud_webapp.dto.TaskRequest;
import com.bsdev.crud_webapp.dto.TaskResponse;
import com.bsdev.crud_webapp.service.TaskService;
import lombok.RequiredArgsConstructor;


import org.spring.bsdev.starter.log_starter.aspect.annotation.AfterReturningLog;
import org.spring.bsdev.starter.log_starter.aspect.annotation.AfterThrowingLog;
import org.spring.bsdev.starter.log_starter.aspect.annotation.AroundLog;
import org.spring.bsdev.starter.log_starter.aspect.annotation.BeforeLog;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @AroundLog
    @AfterReturningLog
    public TaskResponse createTask(@RequestBody TaskRequest taskRequest) {
        return taskService.createTask(taskRequest);
    }

    @GetMapping("/{id}")
    @AfterReturningLog
    @AfterThrowingLog
    public TaskResponse getTask(@PathVariable long id) {
        return taskService.findTaskByIdOrThrow(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @BeforeLog
    @AfterThrowingLog
    public void updateTask(@PathVariable long id, @RequestBody TaskRequest taskRequest) {
        taskService.updateTask(id, taskRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @BeforeLog
    @AfterThrowingLog
    public void deleteTask(@PathVariable long id) {
        taskService.deleteTask(id);
    }

    @GetMapping
    @AfterReturningLog
    public List<TaskResponse> getAllTasks() {
        return taskService.findAllTasks();
    }
}
