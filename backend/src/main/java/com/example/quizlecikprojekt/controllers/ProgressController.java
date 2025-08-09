package com.example.quizlecikprojekt.controllers;

import com.example.quizlecikprojekt.domain.progress.EvaluationService;
import com.example.quizlecikprojekt.domain.progress.Progress;
import com.example.quizlecikprojekt.domain.progress.ProgressService;
import com.example.quizlecikprojekt.domain.progress.ResourceEvaluation;
import com.example.quizlecikprojekt.domain.progress.dto.*;
import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/progress")
@CrossOrigin(origins = "http://localhost:3000")
public class ProgressController {

  private static final Logger logger = LoggerFactory.getLogger(ProgressController.class);

  private final ProgressService progressService;
  private final EvaluationService evaluationService;
  private final UserService userService;

  public ProgressController(
      ProgressService progressService,
      EvaluationService evaluationService,
      UserService userService) {
    this.progressService = progressService;
    this.evaluationService = evaluationService;
    this.userService = userService;
  }

  @PostMapping("/record-session")
  public ResponseEntity<DailyProgressResponse> recordStudySession(
      @Valid @RequestBody RecordStudySessionRequest request, Authentication authentication) {

    User user = userService.getUserByEmail(authentication.getName());
    Progress progress = progressService.recordStudySession(user, request);

    DailyProgressResponse response =
        new DailyProgressResponse(
            progress.getStudyDate(),
            progress.getTotalWordsStudied(),
            progress.getCorrectAnswers(),
            progress.getIncorrectAnswers(),
            progress.getFlashcardsCompleted(),
            progress.getQuizzesCompleted(),
            progress.getStudyTimeMinutes(),
            progress.getAccuracyPercentage());

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/summary")
  public ResponseEntity<ProgressSummaryResponse> getProgressSummary(Authentication authentication) {

    User user = userService.getUserByEmail(authentication.getName());
    ProgressSummaryResponse summary = progressService.getProgressSummary(user);

    return ResponseEntity.ok(summary);
  }

  @GetMapping("/weekly")
  public ResponseEntity<List<WeeklyProgressResponse>> getWeeklyProgress(
      @RequestParam(defaultValue = "12") int weeks, Authentication authentication) {

    User user = userService.getUserByEmail(authentication.getName());
    List<WeeklyProgressResponse> weeklyProgress = progressService.getWeeklyProgress(user, weeks);

    return ResponseEntity.ok(weeklyProgress);
  }

  @GetMapping("/stats")
  public ResponseEntity<ProgressStatsResponse> getProgressStats(Authentication authentication) {

    User user = userService.getUserByEmail(authentication.getName());
    ProgressStatsResponse stats = progressService.getProgressStats(user);

    return ResponseEntity.ok(stats);
  }

  @PostMapping("/evaluate")
  public ResponseEntity<EvaluationResponse> evaluateResource(
      @Valid @RequestBody EvaluateResourceRequest request, Authentication authentication) {

    User user = userService.getUserByEmail(authentication.getName());
    ResourceEvaluation evaluation = evaluationService.evaluateResource(user, request);

    String resourceType = evaluation.getWordSet() != null ? "wordset" : "video";
    Long resourceId =
        evaluation.getWordSet() != null
            ? evaluation.getWordSet().getId()
            : evaluation.getVideo().getId();
    String resourceTitle =
        evaluation.getWordSet() != null
            ? evaluation.getWordSet().getTitle()
            : evaluation.getVideo().getTitle();

    EvaluationResponse response =
        new EvaluationResponse(
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

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/evaluations")
  public ResponseEntity<List<EvaluationResponse>> getUserEvaluations(
      Authentication authentication) {

    User user = userService.getUserByEmail(authentication.getName());
    List<EvaluationResponse> evaluations = evaluationService.getUserEvaluations(user);

    return ResponseEntity.ok(evaluations);
  }

  @GetMapping("/evaluations/wordset/{wordSetId}")
  public ResponseEntity<ResourceEvaluationSummary> getWordSetEvaluations(
      @PathVariable Long wordSetId) {

    ResourceEvaluationSummary summary = evaluationService.getWordSetEvaluationSummary(wordSetId);
    return ResponseEntity.ok(summary);
  }

  @GetMapping("/evaluations/video/{videoId}")
  public ResponseEntity<ResourceEvaluationSummary> getVideoEvaluations(@PathVariable Long videoId) {

    ResourceEvaluationSummary summary = evaluationService.getVideoEvaluationSummary(videoId);
    return ResponseEntity.ok(summary);
  }

  @GetMapping("/evaluations/my-evaluation/wordset/{wordSetId}")
  public ResponseEntity<EvaluationResponse> getMyWordSetEvaluation(
      @PathVariable Long wordSetId, Authentication authentication) {

    User user = userService.getUserByEmail(authentication.getName());
    Optional<ResourceEvaluation> evaluation =
        evaluationService.getUserEvaluationForWordSet(user, wordSetId);

    if (evaluation.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    EvaluationResponse response =
        new EvaluationResponse(
            evaluation.get().getId(),
            "wordset",
            wordSetId,
            evaluation.get().getWordSet().getTitle(),
            evaluation.get().getRating(),
            evaluation.get().getUsefulnessRating(),
            evaluation.get().getDifficultyLevel(),
            evaluation.get().getComment(),
            evaluation.get().getWouldRecommend(),
            evaluation.get().getCompletionTimeMinutes(),
            evaluation.get().getTags(),
            user.getName(),
            evaluation.get().getCreatedAt());

    return ResponseEntity.ok(response);
  }

  @GetMapping("/evaluations/highly-rated")
  public ResponseEntity<List<EvaluationResponse>> getHighlyRatedResources() {
    List<EvaluationResponse> highlyRated = evaluationService.getHighlyRatedResources();
    return ResponseEntity.ok(highlyRated);
  }
}
