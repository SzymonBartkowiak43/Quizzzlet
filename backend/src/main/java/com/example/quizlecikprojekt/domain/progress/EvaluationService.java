package com.example.quizlecikprojekt.domain.progress;

import com.example.quizlecikprojekt.domain.progress.dto.EvaluateResourceRequest;
import com.example.quizlecikprojekt.domain.progress.dto.EvaluationResponse;
import com.example.quizlecikprojekt.domain.progress.dto.ResourceEvaluationSummary;
import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.video.Video;
import com.example.quizlecikprojekt.domain.video.VideoService;
import com.example.quizlecikprojekt.domain.wordset.WordSet;
import com.example.quizlecikprojekt.domain.wordset.WordSetFacade;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class EvaluationService {

  private final ResourceEvaluationRepository evaluationRepository;
  private final WordSetFacade wordSetFacade;
  private final VideoService videoService;

  public ResourceEvaluation evaluateResource(User user, EvaluateResourceRequest request) {
    if (request.wordSetId() == null && request.videoId() == null) {
      throw new IllegalArgumentException("Either wordSetId or videoId must be provided");
    }

    if (request.wordSetId() != null && request.videoId() != null) {
      throw new IllegalArgumentException("Cannot evaluate both wordset and video in one request");
    }

    ResourceEvaluation evaluation;

    if (request.wordSetId() != null) {
      // Evaluate word set
      WordSet wordSet = wordSetFacade.getWordSetById(request.wordSetId());
      Optional<ResourceEvaluation> existing =
          evaluationRepository.findByUserIdAndWordSetId(user.getId(), request.wordSetId());

      if (existing.isPresent()) {
        evaluation = existing.get();
      } else {
        evaluation = new ResourceEvaluation();
        evaluation.setUser(user);
        evaluation.setWordSet(wordSet);
      }
    } else {
      // Evaluate video
      Video video = videoService.findById(request.videoId());
      Optional<ResourceEvaluation> existing =
          evaluationRepository.findByUserIdAndVideoId(user.getId(), request.videoId());

      if (existing.isPresent()) {
        evaluation = existing.get();
      } else {
        evaluation = new ResourceEvaluation();
        evaluation.setUser(user);
        evaluation.setVideo(video);
      }
    }

    // Update evaluation fields
    evaluation.setRating(request.rating());
    evaluation.setUsefulnessRating(request.usefulnessRating());
    evaluation.setComment(request.comment());
    evaluation.setWouldRecommend(Optional.ofNullable(request.wouldRecommend()).orElse(false));
    evaluation.setCompletionTimeMinutes(request.completionTimeMinutes());
    evaluation.setTags(request.tags());

    if (request.difficultyLevel() != null) {
      try {
        evaluation.setDifficultyLevel(
            ResourceEvaluation.DifficultyLevel.valueOf(request.difficultyLevel().toUpperCase()));
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException(
            "Invalid difficulty level: " + request.difficultyLevel());
      }
    }

    return evaluationRepository.save(evaluation);
  }

  public List<EvaluationResponse> getUserEvaluations(User user) {
    List<ResourceEvaluation> evaluations =
        evaluationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

    return evaluations.stream().map(this::mapToEvaluationResponse).collect(Collectors.toList());
  }

  public ResourceEvaluationSummary getWordSetEvaluationSummary(Long wordSetId) {
    WordSet wordSet = wordSetFacade.getWordSetById(wordSetId);
    List<ResourceEvaluation> evaluations =
        evaluationRepository.findByWordSetIdOrderByCreatedAtDesc(wordSetId);

    if (evaluations.isEmpty()) {
      return new ResourceEvaluationSummary(
          "wordset", wordSetId, wordSet.getTitle(), 0.0, 0.0, 0, null, 0.0, List.of());
    }

    Double averageRating = evaluationRepository.getAverageRatingForWordSet(wordSetId);
    Double averageUsefulness = evaluationRepository.getAverageUsefulnessForWordSet(wordSetId);
    Long recommendationCount = evaluationRepository.getRecommendationCountForWordSet(wordSetId);
    Long totalEvaluations = evaluationRepository.getTotalEvaluationsForWordSet(wordSetId);

    double recommendationPercentage =
        totalEvaluations > 0 ? (double) recommendationCount / totalEvaluations * 100.0 : 0.0;

    List<EvaluationResponse> recentEvaluations =
        evaluations.stream()
            .limit(5)
            .map(this::mapToEvaluationResponse)
            .collect(Collectors.toList());

    return new ResourceEvaluationSummary(
        "wordset",
        wordSetId,
        wordSet.getTitle(),
        averageRating != null ? averageRating : 0.0,
        averageUsefulness != null ? averageUsefulness : 0.0,
        totalEvaluations.intValue(),
        getMostCommonDifficulty(wordSetId),
        recommendationPercentage,
        recentEvaluations);
  }

  public ResourceEvaluationSummary getVideoEvaluationSummary(Long videoId) {
    Video video = videoService.findById(videoId);
    List<ResourceEvaluation> evaluations =
        evaluationRepository.findByVideoIdOrderByCreatedAtDesc(videoId);

    if (evaluations.isEmpty()) {
      return new ResourceEvaluationSummary(
          "video", videoId, video.getTitle(), 0.0, 0.0, 0, null, 0.0, List.of());
    }

    Double averageRating = evaluationRepository.getAverageRatingForVideo(videoId);

    double averageUsefulness =
        evaluations.stream()
            .mapToInt(ResourceEvaluation::getUsefulnessRating)
            .average()
            .orElse(0.0);

    long recommendationCount =
        evaluations.stream().mapToLong(e -> e.getWouldRecommend() ? 1 : 0).sum();

    double recommendationPercentage =
            !evaluations.isEmpty() ? (double) recommendationCount / evaluations.size() * 100.0 : 0.0;

    List<EvaluationResponse> recentEvaluations =
        evaluations.stream()
            .limit(5)
            .map(this::mapToEvaluationResponse)
            .collect(Collectors.toList());

    return new ResourceEvaluationSummary(
        "video",
        videoId,
        video.getTitle(),
        averageRating != null ? averageRating : 0.0,
        averageUsefulness,
        evaluations.size(),
        getMostCommonDifficultyForVideo(evaluations),
        recommendationPercentage,
        recentEvaluations);
  }

  public List<EvaluationResponse> getHighlyRatedResources() {
    List<ResourceEvaluation> highlyRated = evaluationRepository.findHighlyRatedResources();

    return highlyRated.stream()
        .limit(20)
        .map(this::mapToEvaluationResponse)
        .collect(Collectors.toList());
  }

  public Optional<ResourceEvaluation> getUserEvaluationForWordSet(User user, Long wordSetId) {
    return evaluationRepository.findByUserIdAndWordSetId(user.getId(), wordSetId);
  }

  private EvaluationResponse mapToEvaluationResponse(ResourceEvaluation evaluation) {
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
        evaluation.getUser().getName(),
        evaluation.getCreatedAt());
  }

  private ResourceEvaluation.DifficultyLevel getMostCommonDifficulty(Long wordSetId) {
    List<Object[]> distribution =
        evaluationRepository.getDifficultyDistributionForWordSet(wordSetId);

    if (!distribution.isEmpty()) {
      return (ResourceEvaluation.DifficultyLevel) distribution.get(0)[0];
    }

    return null;
  }

  private ResourceEvaluation.DifficultyLevel getMostCommonDifficultyForVideo(
      List<ResourceEvaluation> evaluations) {
    return evaluations.stream()
        .filter(e -> e.getDifficultyLevel() != null)
        .collect(
            Collectors.groupingBy(ResourceEvaluation::getDifficultyLevel, Collectors.counting()))
        .entrySet()
        .stream()
        .max(java.util.Map.Entry.comparingByValue())
        .map(java.util.Map.Entry::getKey)
        .orElse(null);
  }

  public Optional<ResourceEvaluation> getUserEvaluationForVideo(User user, Long videoId) {
    return evaluationRepository.findByUserIdAndVideoId(user.getId(), videoId);
  }
}
