package com.example.quizlecikprojekt.domain.user.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class PasswordValidationException extends RuntimeException {
  private final List<String> violations;
  private final String errorCode;

  public PasswordValidationException(String errorCode, List<String> violations) {
    super("Password validation failed");
    this.errorCode = errorCode;
    this.violations = violations;
  }
}
