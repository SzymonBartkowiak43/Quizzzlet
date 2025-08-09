package com.example.quizlecikprojekt.newweb.dto.learn;

public record FlashcardResponse(
        Long wordId,
        String word,
        String translation,
        boolean isRevealed
) {}