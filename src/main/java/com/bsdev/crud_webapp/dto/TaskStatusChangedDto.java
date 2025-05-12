package com.bsdev.crud_webapp.dto;

import com.bsdev.crud_webapp.entity.TaskStatus;

public record TaskStatusChangedDto(
        long taskId,
        TaskStatus status
) { }
