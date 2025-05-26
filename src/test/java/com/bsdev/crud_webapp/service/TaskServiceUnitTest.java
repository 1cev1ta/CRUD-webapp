package com.bsdev.crud_webapp.service;

import com.bsdev.crud_webapp.dto.TaskRequest;
import com.bsdev.crud_webapp.dto.TaskResponse;
import com.bsdev.crud_webapp.dto.TaskStatusChangedDto;
import com.bsdev.crud_webapp.entity.Task;
import com.bsdev.crud_webapp.entity.TaskStatus;
import com.bsdev.crud_webapp.exception.TaskNotFoundException;
import com.bsdev.crud_webapp.repository.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceUnitTest {

    @Mock
    private TaskRepository taskRepositoryMock;

    @InjectMocks
    private TaskService taskService;

    @Mock
    private KafkaTemplate<String, TaskStatusChangedDto> kafkaTemplate;


    @Test
    @DisplayName("тест успешного создания новой задачи")
    void createTaskSuccess(){
        Task exampleTask = createExampleTask(50L, TaskStatus.NEW);
        when(taskRepositoryMock.save(any(Task.class))).thenReturn(exampleTask);

        TaskRequest request = new TaskRequest(
                "Задача №1",
                "Погулять с собакой",
                1L,
                TaskStatus.NEW
        );

        var response = taskService.createTask(request);

        assertNotNull(response, "Ответ не должен быть null");
        assertEquals(exampleTask.getId(), response.id());
        assertEquals(exampleTask.getTitle(), response.title());
        verify(taskRepositoryMock, times(1)).save(any());
    }

    @Test
    @DisplayName("тест успешного поиска задачи по id")
    void findTaskByIdOrThrowException(){
        Task exampleTask = createExampleTask(10L, TaskStatus.IN_PROGRESS);
        when(taskRepositoryMock.findById(exampleTask.getId())).thenReturn(Optional.of(exampleTask));

        var response = taskService.findTaskByIdOrThrow(exampleTask.getId());
        assertEquals(exampleTask.getTitle(), response.title());
        assertEquals(exampleTask.getStatus(), response.status());
    }

    @Test
    @DisplayName("тест НЕ успешного поиска задачи по id")
    void findTaskByIdOrThrowNotFoundException() {
        when(taskRepositoryMock.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class,
                () -> taskService.findTaskByIdOrThrow(99L),
                "Когда задачи с нужным id нет должно выбрасываться TaskNotFoundException");
    }

    @Test
    @DisplayName("тест успешного обновления задачи с новым статусом и публикации в Kafka")
    void updateTaskStatusChangedPublishEvent() {
        Task exampleTask = createExampleTask(3L, TaskStatus.IN_PROGRESS);
        when(taskRepositoryMock.findById(exampleTask.getId())).thenReturn(Optional.of(exampleTask));

        TaskRequest request = new TaskRequest("NewTitle", "NewDesc", 5L, TaskStatus.DONE);
        taskService.updateTask(3L, request);

        verify(taskRepositoryMock).save(argThat(t ->
                t.getStatus() == TaskStatus.DONE &&
                        t.getTitle().equals("NewTitle"))
        );

        verify(kafkaTemplate).sendDefault(
                new TaskStatusChangedDto(3L, TaskStatus.DONE)
        );
    }

    @Test
    @DisplayName("тест удаления задачи с несуществующим id")
    void deleteTaskOrThrowNotFoundException(){
        when(taskRepositoryMock.findById(44L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class,
                () -> taskService.deleteTask(44L),
                "При попытке удаления задачи с несуществующим id выбрасывается TaskNotFoundException");
    }

    @Test
    @DisplayName("тест успешного получения списка всех задач")
    void findAllTasksReturnsList() {
        Task exampleTask = createExampleTask(8L, TaskStatus.DONE);
        when(taskRepositoryMock.findAll()).thenReturn(List.of(exampleTask));

        List<TaskResponse> all = taskService.findAllTasks();
        assertEquals(1, all.size());
        assertEquals("Example Title", all.get(0).title());
    }

    private static Task createExampleTask(long id, TaskStatus status) {
        return Task.builder()
                .id(id)
                .title("Example Title")
                .description("Example Description")
                .userId(100L)
                .status(status)
                .build();
    }
}
