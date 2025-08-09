package com.example.quizlecikprojekt.domain.progress.dto;

public record MonthlyProgressResponse(
        String month,
        Integer year,
        Integer totalWordsStudied,
        Integer totalStudyTimeMinutes,
        Double averageAccuracy,
        Integer daysStudied
) {}