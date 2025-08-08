package com.example.quizlecikprojekt.newweb;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(org.springframework.dao.DataIntegrityViolationException ex) {
        String message = "Unique constraint violated";
        String mostSpecific = org.springframework.core.NestedExceptionUtils.getMostSpecificCause(ex).getMessage();

        if (mostSpecific != null && mostSpecific.contains("(email)")) {
            message = "Email already exists";
        } else if (mostSpecific != null && mostSpecific.contains("(name)")) {
            message = "Username already exists";
        }

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", message, "status", "CONFLICT"));
    }
}