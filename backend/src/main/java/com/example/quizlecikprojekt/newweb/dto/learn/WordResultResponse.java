package com.example.quizlecikprojekt.newweb.dto.learn;

public record WordResultResponse(
        Long wordId,
        String word,
        String translation,
        boolean wasCorrect
) {}
