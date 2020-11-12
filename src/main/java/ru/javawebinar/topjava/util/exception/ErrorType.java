package ru.javawebinar.topjava.util.exception;

public enum ErrorType {
    APP_ERROR("Application error"),
    VALIDATION_ERROR("Validation error"),
    DATA_NOT_FOUND("Data not found"),
    DATA_ERROR("Data error"),
    CONFLICT("Conflict"),
    TEMPORARILY_UNAVAILABLE("Temporarily unavailable");

    private final String name;

    ErrorType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}