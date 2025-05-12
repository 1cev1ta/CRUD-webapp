package com.bsdev.crud_webapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;

    @Value("${notification.from}")
    private String from;

    @Value("${notification.to}")
    private String defaultRecipient;

    public void sendStatusChangedEmail(long taskId, String status) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(defaultRecipient);
        msg.setSubject("Task " + taskId + " status changed");
        msg.setText("Task with ID " + taskId + " has new status: " + status);
        mailSender.send(msg);
    }
}