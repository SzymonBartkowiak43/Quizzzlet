package com.example.quizlecikprojekt.newweb.dto;

public record FlashCardStartResponse (
   String sessionId,
    int totalWords,
    int currentIndex,
    WordResponse currentWord
)
    {}