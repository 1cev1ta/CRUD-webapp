package com.bsdev.crud_webapp.kafka;

import com.bsdev.crud_webapp.dto.TaskStatusChangedDto;
import com.bsdev.crud_webapp.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class TaskStatusConsumer {
    private final NotificationService notificationService;

    @KafkaListener(
            topics = "${spring.kafka.listener.topic.task-status-changed}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(List<TaskStatusChangedDto> dtos, Acknowledgment ack) {
        log.info("Получена пачка обновлений статусов задач: size={}", dtos.size());

        for (TaskStatusChangedDto dto : dtos) {
            notificationService.sendStatusChangedEmail(dto.taskId(), dto.status().name());
            log.debug("Уведомление отправлено для taskId={}", dto.taskId());
        }
        ack.acknowledge();
        log.info("Пачка сообщений подтверждена");
    }
}
