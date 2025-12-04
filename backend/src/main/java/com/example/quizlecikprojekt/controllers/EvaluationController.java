package com.example.quizlecikprojekt.controllers;

import com.example.quizlecikprojekt.domain.progress.EvaluationFacade;
import com.example.quizlecikprojekt.domain.progress.dto.EvaluateResourceRequest;
import com.example.quizlecikprojekt.domain.progress.dto.EvaluationResponse;
import com.example.quizlecikprojekt.domain.progress.dto.ResourceEvaluationSummary;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/evaluations")
@CrossOrigin(origins = "http://68.183.66.208:3000")
public class EvaluationController {

  private final EvaluationFacade evaluationFacade;

  public EvaluationController(EvaluationFacade evaluationFacade) {
    this.evaluationFacade = evaluationFacade;
  }

  @PostMapping
  public ResponseEntity<EvaluationResponse> evaluateResource(
      @Valid @RequestBody EvaluateResourceRequest request, Authentication authentication) {

    EvaluationResponse response =
        evaluationFacade.evaluateResource(authentication.getName(), request);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/my-evaluations")
  public ResponseEntity<List<EvaluationResponse>> getUserEvaluations(
      Authentication authentication) {

    List<EvaluationResponse> evaluations =
        evaluationFacade.getUserEvaluations(authentication.getName());

    return ResponseEntity.ok(evaluations);
  }

  @GetMapping("/wordset/{wordSetId}")
  public ResponseEntity<ResourceEvaluationSummary> getWordSetEvaluations(
      @PathVariable Long wordSetId) {

    ResourceEvaluationSummary summary = evaluationFacade.getWordSetEvaluationSummary(wordSetId);
    return ResponseEntity.ok(summary);
  }

  @GetMapping("/video/{videoId}")
  public ResponseEntity<ResourceEvaluationSummary> getVideoEvaluations(@PathVariable Long videoId) {

    ResourceEvaluationSummary summary = evaluationFacade.getVideoEvaluationSummary(videoId);
    return ResponseEntity.ok(summary);
  }

  @GetMapping("/my-evaluation/wordset/{wordSetId}")
  public ResponseEntity<EvaluationResponse> getMyWordSetEvaluation(
      @PathVariable Long wordSetId, Authentication authentication) {

    Optional<EvaluationResponse> evaluation =
        evaluationFacade.getMyWordSetEvaluation(authentication.getName(), wordSetId);

    return evaluation.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/my-evaluation/video/{videoId}")
  public ResponseEntity<EvaluationResponse> getMyVideoEvaluation(
      @PathVariable Long videoId, Authentication authentication) {

    Optional<EvaluationResponse> evaluation =
        evaluationFacade.getMyVideoEvaluation(authentication.getName(), videoId);

    return evaluation.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/highly-rated")
  public ResponseEntity<List<EvaluationResponse>> getHighlyRatedResources() {
    List<EvaluationResponse> highlyRated = evaluationFacade.getHighlyRatedResources();
    return ResponseEntity.ok(highlyRated);
  }
}
