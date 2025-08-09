package com.example.quizlecikprojekt.controllers.dto.learn;

public record QuizSessionResponse(
    String sessionId,
    Long wordSetId,
    String wordSetTitle,
    int totalQuestions,
    int currentQuestion,
    int score,
    boolean isCompleted,
    QuizQuestionResponse currentQuestions) {}
