package com.example.quizlecikprojekt.exception;

import static org.springframework.core.NestedExceptionUtils.getMostSpecificCause;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.example.quizlecikprojekt.domain.user.exception.PasswordValidationException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
        ex.getConstraintViolations().stream().map(this::formatConstraintViolation).toList();

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
    String mostSpecific = getMostSpecificCause(ex).getMessage();

    if (mostSpecific != null && mostSpecific.contains("(email)")) {
      message = "Email already exists";
    } else if (mostSpecific != null && mostSpecific.contains("(name)")) {
      message = "Username already exists";
    }

    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(Map.of("message", message, "status", "CONFLICT"));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .contentType(APPLICATION_JSON)
        .body(
            Map.of(
                "message",
                "Request contains invalid fields",
                "status",
                HttpStatus.BAD_REQUEST.name()));
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<Map<String, Object>> handleNoSuchElementException(
      NoSuchElementException ex) {
    Map<String, Object> errorResponse =
        Map.of("message", "Requested resource not found", "status", HttpStatus.NOT_FOUND.name());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
    Map<String, Object> errorResponse =
        Map.of(
            "message", "Bad Credentials",
            "status", "UNAUTHORIZED");
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
    ex.printStackTrace();

    Map<String, Object> errorResponse =
        Map.of(
            "message",
            "An unexpected error occurred",
            "status",
            HttpStatus.INTERNAL_SERVER_ERROR.name());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }

  // NEW: Handle EntityNotFoundException (JPA)
  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleEntityNotFoundException(
      EntityNotFoundException ex) {
    Map<String, Object> errorResponse =
        Map.of("message", "Requested resource not found", "status", HttpStatus.NOT_FOUND.name());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
    System.err.println("Access denied: " + ex.getMessage()); // Debug log

    Map<String, Object> errorResponse = Map.of(
            "message", "You don't have permission to access this resource",
            "status", HttpStatus.FORBIDDEN.name()
    );
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
    System.err.println("IllegalArgumentException: " + ex.getMessage());

    String message = ex.getMessage();

    // Check if it's related to resource not found
    if (message != null &&
            (message.toLowerCase().contains("not found") ||
                    message.toLowerCase().contains("does not exist") ||
                    message.toLowerCase().contains("invalid id"))) {

      Map<String, Object> errorResponse = Map.of(
              "message", "Requested resource not found",
              "status", HttpStatus.NOT_FOUND.name()
      );
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    // Check if it's access-related
    if (message != null &&
            (message.toLowerCase().contains("permission") ||
                    message.toLowerCase().contains("access") ||
                    message.toLowerCase().contains("denied"))) {

      Map<String, Object> errorResponse = Map.of(
              "message", "You don't have permission to access this resource",
              "status", HttpStatus.FORBIDDEN.name()
      );
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    // This is the key part - return the actual message for validation errors
    Map<String, Object> errorResponse = Map.of(
            "message", message != null ? message : "Invalid request parameter",
            "status", HttpStatus.BAD_REQUEST.name()
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }


  private String formatConstraintViolation(ConstraintViolation<?> cv) {
    return cv.getPropertyPath() + ": " + cv.getMessage();
  }
}
