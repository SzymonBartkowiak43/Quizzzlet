package com.example.quizlecikprojekt.exception;

import com.example.quizlecikprojekt.domain.user.exception.PasswordValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.core.NestedExceptionUtils.getMostSpecificCause;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(PasswordValidationException.class)
  public ResponseEntity<?> handlePasswordValidationException(PasswordValidationException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            Map.of(
                "errorCode", ex.getErrorCode(),
                "message", "Password does not meet complexity requirements",
                "violations", ex.getViolations()));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException ex) {
    List<String> violations =
        ex.getConstraintViolations().stream()
            .map(this::formatConstraintViolation)
            .collect(Collectors.toList());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            Map.of(
                "errorCode", "404",
                "message", "Request contains invalid fields",
                "violations", violations));
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Map<String, Object>> handleDataIntegrity(
          DataIntegrityViolationException ex) {
    String message = "Unique constraint violated";
    String mostSpecific =
            getMostSpecificCause(ex).getMessage();

    if (mostSpecific != null && mostSpecific.contains("(email)")) {
      message = "Email already exists";
    } else if (mostSpecific != null && mostSpecific.contains("(name)")) {
      message = "Username already exists";
    }

    return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(Map.of("message", message, "status", "CONFLICT"));
  }

  private String formatConstraintViolation(ConstraintViolation<?> cv) {
    return cv.getPropertyPath() + ": " + cv.getMessage();
  }
}
