package com.example.quizlecikprojekt.domain.progress;

import com.example.quizlecikprojekt.domain.progress.dto.EvaluateResourceRequest;
import com.example.quizlecikprojekt.domain.progress.dto.EvaluationResponse;
import com.example.quizlecikprojekt.domain.progress.dto.ResourceEvaluationSummary;
import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserService;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EvaluationFacade {

  private static final Logger logger = LoggerFactory.getLogger(EvaluationFacade.class);

  private final EvaluationService evaluationService;
  private final UserService userService;

  public EvaluationFacade(EvaluationService evaluationService, UserService userService) {
    this.evaluationService = evaluationService;
    this.userService = userService;
  }

  public EvaluationResponse evaluateResource(String userEmail, EvaluateResourceRequest request) {
    logger.info("Evaluating resource for user: {}", userEmail);

    User user = userService.getUserByEmail(userEmail);
    ResourceEvaluation evaluation = evaluationService.evaluateResource(user, request);

    return mapToEvaluationResponse(evaluation, user);
  }

  public List<EvaluationResponse> getUserEvaluations(String userEmail) {
    logger.info("Getting evaluations for user: {}", userEmail);

    User user = userService.getUserByEmail(userEmail);
    return evaluationService.getUserEvaluations(user);
  }

  public ResourceEvaluationSummary getWordSetEvaluationSummary(Long wordSetId) {
    logger.info("Getting word set evaluation summary for ID: {}", wordSetId);

    return evaluationService.getWordSetEvaluationSummary(wordSetId);
  }

  public ResourceEvaluationSummary getVideoEvaluationSummary(Long videoId) {
    logger.info("Getting video evaluation summary for ID: {}", videoId);

    return evaluationService.getVideoEvaluationSummary(videoId);
  }

  public Optional<EvaluationResponse> getMyWordSetEvaluation(String userEmail, Long wordSetId) {
    logger.info("Getting user evaluation for word set {} by user: {}", wordSetId, userEmail);

    User user = userService.getUserByEmail(userEmail);
    Optional<ResourceEvaluation> evaluation =
        evaluationService.getUserEvaluationForWordSet(user, wordSetId);

    return evaluation.map(eval -> mapToEvaluationResponse(eval, user));
  }

  public Optional<EvaluationResponse> getMyVideoEvaluation(String userEmail, Long videoId) {
    logger.info("Getting user evaluation for video {} by user: {}", videoId, userEmail);

    User user = userService.getUserByEmail(userEmail);
    Optional<ResourceEvaluation> evaluation =
        evaluationService.getUserEvaluationForVideo(user, videoId);

    return evaluation.map(eval -> mapToEvaluationResponse(eval, user));
  }

  public List<EvaluationResponse> getHighlyRatedResources() {
    logger.info("Getting highly rated resources");

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
