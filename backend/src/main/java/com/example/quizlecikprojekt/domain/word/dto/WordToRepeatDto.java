package com.example.quizlecikprojekt.domain.word.dto;


public record WordToRepeatDto(
        String word,
        String translation,
        boolean isCorrect
) {
}
