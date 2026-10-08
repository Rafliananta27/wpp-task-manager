package com.rafli.taskmanager.web;

import com.rafli.taskmanager.service.DomainException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

@RestControllerAdvice
public class ApiErrorHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiErrorHandler.class);

    public record ApiError(String error, String message, String field) {
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiError> domain(DomainException ex) {
        int status = ex.kind() == DomainException.Kind.NOT_FOUND ? 404 : 400;
        return ResponseEntity.status(status).body(new ApiError(ex.kind().name(), ex.getMessage(), ex.field()));
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        String message = status.value() == 400 ? "Invalid request: check JSON, parameter types and request body"
                : "Request could not be processed";
        return new ResponseEntity<>(new ApiError("HTTP_" + status.value(), message, null), headers, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> unexpected(Exception ex) {
        log.error("Unexpected API failure", ex);
        return ResponseEntity.status(500).body(new ApiError("INTERNAL_ERROR", "An unexpected error occurred", null));
    }

    @RestController
    public static class FallbackErrors implements ErrorController {
        @RequestMapping("/error")
        public ResponseEntity<ApiError> error(HttpServletRequest request) {
            Object value = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
            int status = value instanceof Integer code ? code : 500;
            return ResponseEntity.status(status).body(new ApiError("HTTP_" + status,
                    status == 404 ? "Resource not found" : "Request could not be processed", null));
        }
    }
}
