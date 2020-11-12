package ru.javawebinar.topjava.util.exception;

import javax.servlet.http.HttpServletRequest;
import java.beans.ConstructorProperties;

public class ErrorInfo {
    private final String url;
    private final String type;
    private final String message;
    private final String exception;

    public ErrorInfo(HttpServletRequest request, Throwable rootCause, ErrorType type) {
        this.url = request.getRequestURL().toString();
        this.type = type.getName();
        this.message = rootCause.getMessage() == null ? "" : rootCause.getMessage();
        this.exception = rootCause.getClass().getName();
    }

    @ConstructorProperties({"url", "type", "message", "exception"})
    public ErrorInfo(String url, String type, String message, String exception) {
        this.url = url;
        this.type = type;
        this.message = message;
        this.exception = exception;
    }

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getException() {
        return exception;
    }
}
