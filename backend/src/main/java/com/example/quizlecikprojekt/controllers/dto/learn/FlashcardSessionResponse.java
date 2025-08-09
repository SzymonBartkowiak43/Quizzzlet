package com.example.quizlecikprojekt.controllers.dto.learn;

public record FlashcardSessionResponse(
    String sessionId,
    Long wordSetId,
    String wordSetTitle,
    int totalWords,
    int currentIndex,
    int score,
    boolean isCompleted,
    FlashcardResponse currentCard) {}
