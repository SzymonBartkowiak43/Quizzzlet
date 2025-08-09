package com.example.quizlecikprojekt.controllers.dto.learn;

public record WordResultResponse(
    Long wordId, String word, String translation, boolean wasCorrect) {}
