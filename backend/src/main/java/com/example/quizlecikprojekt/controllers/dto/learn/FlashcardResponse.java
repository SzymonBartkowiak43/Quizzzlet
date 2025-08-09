package com.example.quizlecikprojekt.controllers.dto.learn;

public record FlashcardResponse(Long wordId, String word, String translation, boolean isRevealed) {}
