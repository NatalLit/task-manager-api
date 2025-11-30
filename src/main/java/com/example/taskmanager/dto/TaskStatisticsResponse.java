package com.example.taskmanager.dto;

public record TaskStatisticsResponse(
        long total,
        long todo,
        long inProgress,
        long done
) {
}
