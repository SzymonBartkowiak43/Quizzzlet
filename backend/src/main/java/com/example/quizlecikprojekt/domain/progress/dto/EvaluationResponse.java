package com.example.quizlecikprojekt.domain.progress.dto;

import com.example.quizlecikprojekt.domain.progress.ResourceEvaluation;

import java.time.LocalDateTime;

public record EvaluationResponse(
        Long id,
        String resourceType,
        Long resourceId,
        String resourceTitle,
        Integer rating,
        Integer usefulnessRating,
        ResourceEvaluation.DifficultyLevel difficultyLevel,
        String comment,
        Boolean wouldRecommend,
        Integer completionTimeMinutes,
        String tags,
        String evaluatorName,
        LocalDateTime createdAt
) {}