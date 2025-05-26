package com.bsdev.crud_webapp.controller;

import com.bsdev.crud_webapp.AbstractContainerBaseTest;
import com.bsdev.crud_webapp.dto.TaskStatusChangedDto;
import com.bsdev.crud_webapp.entity.Task;
import com.bsdev.crud_webapp.entity.TaskStatus;
import com.bsdev.crud_webapp.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TaskControllerTest extends AbstractContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository repository;

    @MockBean
    private KafkaTemplate<String, TaskStatusChangedDto> kafkaTemplate;

    private long existingId;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        var saved = repository.save(
                Task.builder()
                        .id(123)
                        .title("Init")
                        .description("Init")
                        .userId(1L)
                        .status(TaskStatus.NEW)
                        .build()
        );
        existingId = saved.getId();
    }

    @Test
    @DisplayName("покрывает сценарий вызова метода POST /tasks для создания новой задачи")
    void createTaskEndpoint() throws Exception {
        String json = """
            {
              "title": "Задача №1",
              "description": "Погулять с собакой",
              "userId": 2542,
              "status": "NEW"
            }
            """;

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Задача №1"));
    }

    @Test
    @DisplayName("покрывает успешный сценарий вызова метода GET /tasks/{id} для получения задачи по ID")
    void getTaskFoundSuccess() throws Exception {
        mockMvc.perform(get("/tasks/{id}", existingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingId));
    }

    @Test
    @DisplayName("покрывает негативный сценарий вызова метода GET /tasks/{id} при отсутствии задачи")
    void getTaskNotFound() throws Exception {
        mockMvc.perform(get("/tasks/{id}", 9999))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("покрывает сценарий вызова метода PUT /tasks/{id} для обновления задачи и публикации события в Kafka")
    void updateTask_changesStatus_andPublishesKafka() throws Exception {
        String json = """
            {
              "title": "Задача №1",
              "description": "Погулять с собакой",
              "userId": 2542,
              "status": "DONE"
            }
            """;

        mockMvc.perform(put("/tasks/{id}", existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        Task updated = repository.findById(existingId)
                .orElseThrow(() -> new AssertionError("Task not found after update"));
        assertEquals("Задача №1",   updated.getTitle(),       "Title должен сохраниться из запроса");
        assertEquals("Погулять с собакой",   updated.getDescription(), "Description должен сохраниться из запроса");
        assertEquals(2542L,    updated.getUserId(),      "UserId должен сохраниться из запроса");
        assertEquals(TaskStatus.DONE, updated.getStatus(), "Status должен обновиться на DONE");

        verify(kafkaTemplate).sendDefault(
                new TaskStatusChangedDto(existingId, TaskStatus.DONE)
        );
    }

    @Test
    @DisplayName("покрывает позитивный сценарий вызова метода DELETE /tasks/{id} для удаления задачи")
    void deleteTaskEndpoint() throws Exception {
        mockMvc.perform(delete("/tasks/{id}", existingId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("покрывает сценарий вызова метода GET /tasks для получения списка всех задач")
    void getAllTasksEndpoint() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}
