package com.example.quizlecikprojekt.newweb.dto;

import com.example.quizlecikprojekt.newweb.dto.word.WordResponse;

public record FlashCardStartResponse(
    String sessionId, int totalWords, int currentIndex, WordResponse currentWord) {}
