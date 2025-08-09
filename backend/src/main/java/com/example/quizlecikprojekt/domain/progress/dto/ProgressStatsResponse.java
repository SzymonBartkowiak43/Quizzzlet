package com.example.quizlecikprojekt.domain.progress.dto;

import java.util.List;

public record ProgressStatsResponse(
    Integer totalDaysStudied,
    Integer currentStreak,
    Integer longestStreak,
    String mostActiveDay,
    String preferredStudyTime,
    WordSetProgressResponse mostStudiedWordSet,
    List<WeeklyProgressResponse> weeklyProgress,
    List<MonthlyProgressResponse> monthlyProgress) {}
