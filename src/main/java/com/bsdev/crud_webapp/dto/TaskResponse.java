package com.bsdev.crud_webapp.dto;

import com.bsdev.crud_webapp.entity.TaskStatus;

public record TaskResponse(
        long id,
        String title,
        String description,
        long userId,
        TaskStatus status
) { }
