package com.example.quizlecikprojekt.domain.user.exception;

import java.util.List;

public class PasswordValidationException extends RuntimeException {
  private final List<String> violations;
  private final String errorCode;

  public PasswordValidationException(String errorCode, List<String> violations) {
    super("Password validation failed");
    this.errorCode = errorCode;
    this.violations = violations;
  }

  public List<String> getViolations() {
    return violations;
  }

  public String getErrorCode() {
    return errorCode;
  }
}
