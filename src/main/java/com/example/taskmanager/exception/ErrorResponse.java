package com.example.taskmanager.exception;

public record ErrorResponse(
        String code,
        String message
) {
}

