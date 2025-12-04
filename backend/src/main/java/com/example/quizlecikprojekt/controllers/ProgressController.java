package com.example.quizlecikprojekt.controllers;

import com.example.quizlecikprojekt.domain.progress.ProgressFacade;
import com.example.quizlecikprojekt.domain.progress.dto.*;
import jakarta.validation.Valid;
import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/progress")
@CrossOrigin(origins = "http://68.183.66.208:80")
public class ProgressController {

  private final ProgressFacade progressFacade;

  @PostMapping("/record-session")
  public ResponseEntity<DailyProgressResponse> recordStudySession(
      @Valid @RequestBody RecordStudySessionRequest request, Authentication authentication) {

    DailyProgressResponse response =
        progressFacade.recordStudySession(authentication.getName(), request);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/summary")
  public ResponseEntity<ProgressSummaryResponse> getProgressSummary(Authentication authentication) {

    ProgressSummaryResponse summary = progressFacade.getProgressSummary(authentication.getName());

    return ResponseEntity.ok(summary);
  }

  @GetMapping("/weekly")
  public ResponseEntity<List<WeeklyProgressResponse>> getWeeklyProgress(
      @RequestParam(defaultValue = "12") int weeks, Authentication authentication) {

    List<WeeklyProgressResponse> weeklyProgress =
        progressFacade.getWeeklyProgress(authentication.getName(), weeks);

    return ResponseEntity.ok(weeklyProgress);
  }

  @GetMapping("/stats")
  public ResponseEntity<ProgressStatsResponse> getProgressStats(Authentication authentication) {

    ProgressStatsResponse stats = progressFacade.getProgressStats(authentication.getName());

    return ResponseEntity.ok(stats);
  }
}
