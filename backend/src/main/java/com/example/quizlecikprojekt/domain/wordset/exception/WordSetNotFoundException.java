package com.example.quizlecikprojekt.domain.wordset.exception;

public class WordSetNotFoundException extends RuntimeException {
    public WordSetNotFoundException(String message) {
        super(message);
    }
}