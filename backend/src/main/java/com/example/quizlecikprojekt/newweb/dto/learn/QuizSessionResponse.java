package com.example.quizlecikprojekt.newweb.dto.learn;

public record QuizSessionResponse(
        String sessionId,
        Long wordSetId,
        String wordSetTitle,
        int totalQuestions,
        int currentQuestion,
        int score,
        boolean isCompleted,
        QuizQuestionResponse currentQuestions

) {}