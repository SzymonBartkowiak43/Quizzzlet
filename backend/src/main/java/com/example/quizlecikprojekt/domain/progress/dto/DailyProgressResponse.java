package com.example.quizlecikprojekt.domain.progress.dto;

import java.time.LocalDate;

public record DailyProgressResponse(
    LocalDate date,
    Integer wordsStudied,
    Integer correctAnswers,
    Integer incorrectAnswers,
    Integer flashcardsCompleted,
    Integer quizzesCompleted,
    Integer studyTimeMinutes,
    Double accuracyPercentage) {}
