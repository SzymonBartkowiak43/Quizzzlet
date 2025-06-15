package com.example.quizlecikprojekt.domain.user;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;


public class PasswordValidator {
    private static final int MIN_LENGTH = 10;
    private static final String LOWERCASE_CONSTRAINT = "Password should contain at least one lowercase letter.";
    private static final String UPPERCASE_CONSTRAINT = "Password should contain at least one uppercase letter.";
    private static final String DIGIT_CONSTRAINT = "Password should contain at least one digit.";
    private static final String LENGTH_CONSTRAINT = "Password should have a minimum length of " + MIN_LENGTH;

    public static List<String> getConstraintViolations(String password) {
        List<String> unsatisfiedConstraints = new ArrayList<>();
        if (!containsLowerCaseLetter(password)) {
            unsatisfiedConstraints.add(LOWERCASE_CONSTRAINT);
        }
        if (!containsUpperCaseLetter(password)) {
            unsatisfiedConstraints.add(UPPERCASE_CONSTRAINT);
        }
        if (!containsDigit(password)) {
            unsatisfiedConstraints.add(DIGIT_CONSTRAINT);
        }
        if (!hasMinimumLength(password)) {
            unsatisfiedConstraints.add(LENGTH_CONSTRAINT);
        }
        return unsatisfiedConstraints;
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
        return password.length() >= MIN_LENGTH;
    }

    private static boolean checkConditionForAllLetters(String password, Predicate<Character> predicate) {
        char[] chars = password.toCharArray();
        for (char ch : chars) {
            if (predicate.test(ch))
                return true;
        }
        return false;
    }
}