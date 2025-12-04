package com.example.quizlecikprojekt.controllers;

import com.example.quizlecikprojekt.controllers.dto.learn.*;
import com.example.quizlecikprojekt.domain.learn.LearnFacade;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/learn")
@CrossOrigin(origins = "http://68.183.66.208:80")
public class LearnRestController {

  private final LearnFacade learnFacade;

  @PostMapping("/flashcards/start")
  public ResponseEntity<FlashcardSessionResponse> startFlashcardSession(
      Authentication authentication, @Valid @RequestBody StartFlashcardRequest request) {

    FlashcardSessionResponse response =
        learnFacade.startFlashcardSession(authentication.getName(), request);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/flashcards/answer")
  public ResponseEntity<FlashcardSessionResponse> answerFlashcard(
      @Valid @RequestBody AnswerFlashcardRequest request) {

    FlashcardSessionResponse response = learnFacade.answerFlashcard(request);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/flashcards/{sessionId}")
  public ResponseEntity<FlashcardSessionResponse> getFlashcardSession(
      @PathVariable String sessionId) {

    FlashcardSessionResponse response = learnFacade.getFlashcardSessionResponse(sessionId);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/flashcards/{sessionId}")
  public ResponseEntity<SessionSummaryResponse> endFlashcardSession(
      @PathVariable String sessionId) {

    SessionSummaryResponse summary = learnFacade.endFlashcardSession(sessionId);
    return ResponseEntity.ok(summary);
  }

  // Quiz endpoints
  @PostMapping("/quiz/start")
  public ResponseEntity<QuizSessionResponse> startQuizSession(
      Authentication authentication, @Valid @RequestBody StartQuizRequest request) {

    QuizSessionResponse response = learnFacade.startQuizSession(authentication.getName(), request);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/quiz/answer")
  public ResponseEntity<QuizSessionResponse> answerQuiz(
      @Valid @RequestBody AnswerQuizRequest request) {

    QuizSessionResponse response = learnFacade.answerQuiz(request);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/quiz/{sessionId}")
  public ResponseEntity<QuizSessionResponse> getQuizSession(@PathVariable String sessionId) {

    QuizSessionResponse response = learnFacade.getQuizSessionResponse(sessionId);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/quiz/{sessionId}")
  public ResponseEntity<SessionSummaryResponse> endQuizSession(@PathVariable String sessionId) {

    SessionSummaryResponse summary = learnFacade.endQuizSession(sessionId);
    return ResponseEntity.ok(summary);
  }
}
