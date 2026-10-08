package com.rafli.taskmanager.service;

public class DomainException extends RuntimeException {
    public enum Kind {
        VALIDATION_FAILED, NOT_FOUND
    }

    private final Kind kind;
    private final String field;

    public DomainException(Kind kind, String message, String field) {
        super(message);
        this.kind = kind;
        this.field = field;
    }

    public Kind kind() {
        return kind;
    }

    public String field() {
        return field;
    }
}
