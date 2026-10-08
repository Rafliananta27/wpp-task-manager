package com.rafli.taskmanager.model;

import java.time.Instant;

public record Task(long id, long boardId, String title, String description, String status,
        Instant createdAt, Instant updatedAt) {
}