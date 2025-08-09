package com.example.quizlecikprojekt.controllers.dto.learn;

import java.util.List;

public record SessionSummaryResponse(
    String sessionId,
    String sessionType,
    Long wordSetId,
    String wordSetTitle,
    int totalItems,
    int correctAnswers,
    int incorrectAnswers,
    double accuracy,
    List<WordResultResponse> incorrectWords,
    String completedAt) {}
