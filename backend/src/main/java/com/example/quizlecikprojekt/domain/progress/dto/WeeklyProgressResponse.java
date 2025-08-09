package com.example.quizlecikprojekt.domain.progress.dto;

import java.util.List;

public record WeeklyProgressResponse(
    String weekRange,
    Integer totalWordsStudied,
    Integer totalCorrectAnswers,
    Integer totalIncorrectAnswers,
    Integer totalStudyTimeMinutes,
    Double averageAccuracy,
    List<DailyProgressResponse> dailyBreakdown) {}
