package pl.bartkowiak.quizlecikprojekt.domain.progress.dto;

import pl.bartkowiak.quizlecikprojekt.entity.ResourceEvaluation;
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
    LocalDateTime createdAt) {}
