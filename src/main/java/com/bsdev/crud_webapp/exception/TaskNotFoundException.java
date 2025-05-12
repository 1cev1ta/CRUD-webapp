package com.bsdev.crud_webapp.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(Long id) {
        super("Задача с id: " + id + " не найдена в базе данных.");
    }
}
