package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.javawebinar.topjava.util.exception.ErrorInfo;
import ru.javawebinar.topjava.util.exception.ErrorType;
import ru.javawebinar.topjava.util.exception.IllegalRequestDataException;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;

import static ru.javawebinar.topjava.util.exception.ErrorType.*;

@RestControllerAdvice(annotations = RestController.class)
public class RestControllerExceptionHandler {

    @ResponseStatus(HttpStatus.LOCKED)
    @ExceptionHandler(UnavailableException.class)
    public ErrorInfo unavailableError(HttpServletRequest request, UnavailableException e) {
        return logAndGetErrorInfo(request, e, false, TEMPORARILY_UNAVAILABLE);
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(NotFoundException.class)
    public ErrorInfo requestDataError(HttpServletRequest request, NotFoundException e) {
        return logAndGetErrorInfo(request, e, false, DATA_NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(IllegalRequestDataException.class)
    public ErrorInfo dataError(HttpServletRequest request, IllegalRequestDataException e) {
        return logAndGetErrorInfo(request, e, false, DATA_ERROR);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({DataIntegrityViolationException.class})
    public ErrorInfo conflict(HttpServletRequest request, DataIntegrityViolationException e) {
        return logAndGetErrorInfo(request, e, true, CONFLICT);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorInfo handleError(HttpServletRequest request, Exception e) {
        return logAndGetErrorInfo(request, e, true, APP_ERROR);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RestControllerExceptionHandler.class);

    private ErrorInfo logAndGetErrorInfo(HttpServletRequest request, Exception e, boolean logStackTrace, ErrorType type) {
        Throwable rootCause = getRootCause(e);
        if (logStackTrace) {
            LOGGER.error(String.format("[%s]", request.getRequestURL()), rootCause);
        } else {
            LOGGER.warn("[{}] {}", request.getRequestURL(), rootCause.toString());
        }
        return new ErrorInfo(request, rootCause, type);
    }

    //  http://stackoverflow.com/a/28565320/548473
    private Throwable getRootCause(Throwable t) {
        Throwable result = t;
        Throwable cause;

        while (null != (cause = result.getCause()) && (result != cause)) {
            result = cause;
        }
        return result;
    }
}
