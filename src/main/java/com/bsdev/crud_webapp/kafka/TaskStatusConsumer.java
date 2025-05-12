package com.bsdev.crud_webapp.kafka;

import com.bsdev.crud_webapp.dto.TaskStatusChangedDto;
import com.bsdev.crud_webapp.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TaskStatusConsumer {
    private final NotificationService notificationService;

    @KafkaListener(
            topics = "${spring.kafka.listener.topic.task-status-changed}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(TaskStatusChangedDto dto, Acknowledgment ack) {
        log.info("Received task status change: {}", dto);
        notificationService.sendStatusChangedEmail(dto.taskId(), dto.status().name());
        ack.acknowledge();
    }
}
