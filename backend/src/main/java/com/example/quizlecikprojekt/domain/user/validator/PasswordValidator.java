package com.example.quizlecikprojekt.domain.user.validator;

import com.example.quizlecikprojekt.exception.PasswordValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import org.springframework.stereotype.Component;

@Component
public class PasswordValidator {

  private static final int MIN_LENGTH = 10;
  private static final String LOWERCASE_CONSTRAINT =
      "Password should contain at least one lowercase letter.";
  private static final String UPPERCASE_CONSTRAINT =
      "Password should contain at least one uppercase letter.";
  private static final String DIGIT_CONSTRAINT = "Password should contain at least one digit.";
  private static final String LENGTH_CONSTRAINT =
      "Password should have a minimum length of " + MIN_LENGTH;

  public static void validate(String password) {
    List<String> violations = new ArrayList<>();
    if (!containsLowerCaseLetter(password)) {
      violations.add(LOWERCASE_CONSTRAINT);
    }
    if (!containsUpperCaseLetter(password)) {
      violations.add(UPPERCASE_CONSTRAINT);
    }
    if (!containsDigit(password)) {
      violations.add(DIGIT_CONSTRAINT);
    }
    if (!hasMinimumLength(password)) {
      violations.add(LENGTH_CONSTRAINT);
    }

    if (!violations.isEmpty()) {
      throw new PasswordValidationException("400", violations);
    }
  }

  private static boolean containsLowerCaseLetter(String password) {
    return checkConditionForAllLetters(password, Character::isLowerCase);
  }

  private static boolean containsUpperCaseLetter(String password) {
    return checkConditionForAllLetters(password, Character::isUpperCase);
  }

  private static boolean containsDigit(String password) {
    return checkConditionForAllLetters(password, Character::isDigit);
  }

  private static boolean hasMinimumLength(String password) {
    return password != null && password.length() >= MIN_LENGTH;
  }

  private static boolean checkConditionForAllLetters(
      String password, Predicate<Character> predicate) {
    if (password == null) return false;
    for (char ch : password.toCharArray()) {
      if (predicate.test(ch)) return true;
    }
    return false;
  }
}
