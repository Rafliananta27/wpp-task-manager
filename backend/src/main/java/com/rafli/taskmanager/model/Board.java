package com.rafli.taskmanager.model;

import java.time.Instant;

public record Board(long id, String name, Instant createdAt) {
}
