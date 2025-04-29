package com.bsdev.crud_webapp.dto;

public record TaskRequest(
        String title,
        String description,
        long userId
) { }
