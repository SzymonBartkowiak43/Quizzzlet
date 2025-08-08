package com.example.quizlecikprojekt.domain.user;

import com.example.quizlecikprojekt.domain.user.exception.PasswordValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PasswordValidationException.class)
    public ResponseEntity<?> handlePasswordValidationException(PasswordValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "errorCode", ex.getErrorCode(),
                        "message", "Password does not meet complexity requirements",
                        "violations", ex.getViolations()
                ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> violations = ex.getConstraintViolations()
                .stream()
                .map(this::formatConstraintViolation)
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "errorCode", "404",
                        "message", "Request contains invalid fields",
                        "violations", violations
                ));
    }

    private String formatConstraintViolation(ConstraintViolation<?> cv) {
        return cv.getPropertyPath() + ": " + cv.getMessage();
    }

}
