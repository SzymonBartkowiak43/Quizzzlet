package com.example.quizlecikprojekt.domain.progress.dto;

import java.time.LocalDate;
import java.util.List;

public record ProgressSummaryResponse(
    Long userId,
    String userName,
    Integer currentStreak,
    Integer longestStreak,
    Integer totalWordsStudied,
    Integer totalFlashcardsCompleted,
    Integer totalQuizzesCompleted,
    Integer totalStudyTimeMinutes,
    Double overallAccuracy,
    LocalDate lastStudyDate,
    List<DailyProgressResponse> recentProgress) {}
