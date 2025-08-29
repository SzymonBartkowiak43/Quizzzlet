package com.example.quizlecikprojekt.domain.progress;

import com.example.quizlecikprojekt.domain.progress.dto.EvaluateResourceRequest;
import com.example.quizlecikprojekt.domain.progress.dto.EvaluationResponse;
import com.example.quizlecikprojekt.domain.progress.dto.ResourceEvaluationSummary;
import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserService;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EvaluationFacade {

  private final EvaluationService evaluationService;
  private final UserService userService;

  public EvaluationResponse evaluateResource(String userEmail, EvaluateResourceRequest request) {
    User user = userService.getUserByEmail(userEmail);
    ResourceEvaluation evaluation = evaluationService.evaluateResource(user, request);

    return mapToEvaluationResponse(evaluation, user);
  }

  public List<EvaluationResponse> getUserEvaluations(String userEmail) {
    User user = userService.getUserByEmail(userEmail);
    return evaluationService.getUserEvaluations(user);
  }

  public ResourceEvaluationSummary getWordSetEvaluationSummary(Long wordSetId) {
    return evaluationService.getWordSetEvaluationSummary(wordSetId);
  }

  public ResourceEvaluationSummary getVideoEvaluationSummary(Long videoId) {
    return evaluationService.getVideoEvaluationSummary(videoId);
  }

  public Optional<EvaluationResponse> getMyWordSetEvaluation(String userEmail, Long wordSetId) {
    User user = userService.getUserByEmail(userEmail);
    Optional<ResourceEvaluation> evaluation =
        evaluationService.getUserEvaluationForWordSet(user, wordSetId);

    return evaluation.map(eval -> mapToEvaluationResponse(eval, user));
  }

  public Optional<EvaluationResponse> getMyVideoEvaluation(String userEmail, Long videoId) {
    User user = userService.getUserByEmail(userEmail);
    Optional<ResourceEvaluation> evaluation =
        evaluationService.getUserEvaluationForVideo(user, videoId);

    return evaluation.map(eval -> mapToEvaluationResponse(eval, user));
  }

  public List<EvaluationResponse> getHighlyRatedResources() {
    return evaluationService.getHighlyRatedResources();
  }

  private EvaluationResponse mapToEvaluationResponse(ResourceEvaluation evaluation, User user) {
    String resourceType = evaluation.getWordSet() != null ? "wordset" : "video";
    Long resourceId =
        evaluation.getWordSet() != null
            ? evaluation.getWordSet().getId()
            : evaluation.getVideo().getId();
    String resourceTitle =
        evaluation.getWordSet() != null
            ? evaluation.getWordSet().getTitle()
            : evaluation.getVideo().getTitle();

    return new EvaluationResponse(
        evaluation.getId(),
        resourceType,
        resourceId,
        resourceTitle,
        evaluation.getRating(),
        evaluation.getUsefulnessRating(),
        evaluation.getDifficultyLevel(),
        evaluation.getComment(),
        evaluation.getWouldRecommend(),
        evaluation.getCompletionTimeMinutes(),
        evaluation.getTags(),
        user.getName(),
        evaluation.getCreatedAt());
  }
}
