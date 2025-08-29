package com.example.quizlecikprojekt.domain.progress.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EvaluateResourceRequest(
    Long wordSetId,
    Long videoId,
    @NotNull(message = "Rating is required")
        @Min(value = 1, message = "Rating must be between 1 and 5")
        @Max(value = 5, message = "Rating must be between 1 and 5")
        Integer rating,
    @NotNull(message = "Usefulness rating is required")
        @Min(value = 1, message = "Usefulness rating must be between 1 and 5")
        @Max(value = 5, message = "Usefulness rating must be between 1 and 5")
        Integer usefulnessRating,
    String difficultyLevel,
    @Size(max = 1000, message = "Comment cannot exceed 1000 characters") String comment,
    Boolean wouldRecommend,
    @Min(value = 1, message = "Completion time must be positive") Integer completionTimeMinutes,
    @Size(max = 200, message = "Tags cannot exceed 200 characters") String tags) {}
