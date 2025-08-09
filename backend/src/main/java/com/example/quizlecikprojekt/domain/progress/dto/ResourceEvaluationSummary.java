package com.example.quizlecikprojekt.domain.progress.dto;

import com.example.quizlecikprojekt.domain.progress.ResourceEvaluation;

import java.util.List;

public record ResourceEvaluationSummary(
        String resourceType,
        Long resourceId,
        String resourceTitle,
        Double averageRating,
        Double averageUsefulnessRating,
        Integer totalEvaluations,
        ResourceEvaluation.DifficultyLevel mostCommonDifficulty,
        Double recommendationPercentage,
        List<EvaluationResponse> recentEvaluations
) {}