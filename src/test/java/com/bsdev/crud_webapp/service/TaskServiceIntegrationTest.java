package com.bsdev.crud_webapp.service;

import com.bsdev.crud_webapp.AbstractContainerBaseTest;
import com.bsdev.crud_webapp.dto.TaskRequest;
import com.bsdev.crud_webapp.dto.TaskResponse;
import com.bsdev.crud_webapp.dto.TaskStatusChangedDto;
import com.bsdev.crud_webapp.entity.TaskStatus;
import com.bsdev.crud_webapp.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
public class TaskServiceIntegrationTest extends AbstractContainerBaseTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @MockBean
    private KafkaTemplate<String, TaskStatusChangedDto> kafkaTemplate;

    @BeforeEach
    public void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    @DisplayName("покрывает метод создания задачи через createTask и сохранение в БД")
    void createTaskIntegration() {
        TaskRequest request = new TaskRequest("Задача №3", "Купить молока", 100L, TaskStatus.NEW);
        var response = taskService.createTask(request);

        assertEquals(1, taskRepository.count(), "В репозитории должна быть одна запись после создания");
        assertEquals("Задача №3", response.title());
        assertEquals("Купить молока", response.description());
        assertEquals(100L, response.userId());
        assertEquals(TaskStatus.NEW, response.status());
    }

    @Test
    @DisplayName("покрывает метод получения задачи по ID через findTaskByIdOrThrow")
    void findTaskByIdOrThrowIntegration() {
        TaskRequest request = new TaskRequest("Задача №5", "Убраться дома", 200L, TaskStatus.IN_PROGRESS);
        var created = taskService.createTask(request);

        var response = taskService.findTaskByIdOrThrow(created.id());
        assertEquals(created.id(), response.id(), "ID совпадает с созданной задачей");
        assertEquals("Задача №5", response.title());
    }

    @Test
    @DisplayName("покрывает метод обновления задачи через updateTask и публикацию в Kafka при смене статуса")
    void updateTaskIntegration() {
        TaskRequest request = new TaskRequest("Задача №8", "Найти работника", 300L, TaskStatus.NEW);
        var created = taskService.createTask(request);

        TaskRequest updateRequest = new TaskRequest("Задача №8 - обновленная", "Найти работника с высшим образованием", 300L, TaskStatus.DONE);
        taskService.updateTask(created.id(), updateRequest);

        var updated = taskRepository.findById(created.id()).orElseThrow();
        assertEquals("Задача №8 - обновленная", updated.getTitle(), "Title должен обновиться");
        assertEquals("Найти работника с высшим образованием", updated.getDescription(), "Description должен обновиться");
        assertEquals(TaskStatus.DONE, updated.getStatus(), "Status должен обновиться");

        verify(kafkaTemplate).sendDefault(new TaskStatusChangedDto(created.id(), TaskStatus.DONE));
        verify(kafkaTemplate).flush();
    }

    @Test
    @DisplayName("покрывает метод удаления задачи через deleteTask и удаление из БД")
    void deleteTaskIntegration() {
        TaskRequest request = new TaskRequest("Задача №10", "Погулять с собакой", 400L, TaskStatus.NEW);
        var created = taskService.createTask(request);

        assertEquals(1, taskRepository.count(), "Перед удалением в репозитории одна задача");
        taskService.deleteTask(created.id());
        assertEquals(0, taskRepository.count(), "После удаления репозиторий пуст");
    }

    @Test
    @DisplayName("покрывает метод получения списка всех задач через findAllTasks")
    void findAllTasksIntegration() {
        taskService.createTask(new TaskRequest("Задача 1", "Описание 1", 1L, TaskStatus.NEW));
        taskService.createTask(new TaskRequest("Задача 2", "Описание 2", 2L, TaskStatus.IN_PROGRESS));

        List<TaskResponse> all = taskService.findAllTasks();
        assertEquals(2, all.size(), "Должно быть две задачи в списке");
    }

}
