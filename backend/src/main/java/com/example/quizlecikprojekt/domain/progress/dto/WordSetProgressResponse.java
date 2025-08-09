package com.example.quizlecikprojekt.domain.progress.dto;

import java.time.LocalDateTime;

public record WordSetProgressResponse(
        Long wordSetId,
        String wordSetTitle,
        Integer timesStudied,
        Integer totalWordsStudied,
        Double averageAccuracy,
        Integer totalStudyTimeMinutes,
        LocalDateTime lastStudied
) {}
