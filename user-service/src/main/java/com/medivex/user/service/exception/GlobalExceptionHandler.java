package com.medivex.user.service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("path", request.getRequestURI());
        body.put("error", "Validation failed");
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("message", ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex, HttpServletRequest request) {
        // Map common business errors to appropriate status codes
        String msg = ex.getMessage() != null ? ex.getMessage() : "Unexpected error";
        HttpStatus status;
        if (msg.contains("already taken") || msg.contains("already in use")) {
            status = HttpStatus.CONFLICT; // 409
        } else if (msg.contains("not found") || msg.contains("Invalid")) {
            status = HttpStatus.BAD_REQUEST; // 400
        } else if (msg.contains("locked")) {
            status = HttpStatus.LOCKED; // 423
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR; // 500
        }
        log.warn("Handled business exception: {} -> {}", msg, status);
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("path", request.getRequestURI());
        body.put("error", status.getReasonPhrase());
        body.put("status", status.value());
        body.put("message", msg);
        return ResponseEntity.status(status).body(body);
    }
}
