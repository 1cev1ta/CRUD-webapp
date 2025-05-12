package com.bsdev.crud_webapp.dto;

public record TaskResponse(
        long id,
        String title,
        String description,
        long userId
) { }
