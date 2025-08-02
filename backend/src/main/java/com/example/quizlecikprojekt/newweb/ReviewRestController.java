package com.example.quizlecikprojekt.newweb;

import com.example.quizlecikprojekt.domain.user.UserService;
import com.example.quizlecikprojekt.domain.word.WordService;
import com.example.quizlecikprojekt.domain.word.dto.WordToRepeatDto;
import com.example.quizlecikprojekt.newweb.dto.ApiResponse;
import com.example.quizlecikprojekt.newweb.dto.review.request.CheckAnswerRequest;
import com.example.quizlecikprojekt.newweb.dto.review.response.*;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/review")
@CrossOrigin(origins = "http://localhost:3000")
public class ReviewRestController {

  private static final Logger logger = LoggerFactory.getLogger(ReviewRestController.class);

  private final WordService wordService;
  private final UserService userService;

  // Session storage dla aktywnych sesji review - w produkcji lepiej użyć Redis
  private final Map<String, ReviewSession> reviewSessions = new ConcurrentHashMap<>();

  public ReviewRestController(WordService wordService, UserService userService) {
    this.wordService = wordService;
    this.userService = userService;
  }

  @PostMapping("/start")
  public ResponseEntity<ApiResponse<ReviewStartResponse>> startReview(
      Authentication authentication) {
    try {
      if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("User not authenticated"));
      }

      String userEmail = authentication.getName();
      Long userId = userService.getUserIdByUsername(userEmail);

      List<WordToRepeatDto> wordsToRepeat = wordService.getWordsToRepeat(userId);

      if (wordsToRepeat.isEmpty()) {
        return ResponseEntity.ok(
            ApiResponse.success(
                "No words to review",
                new ReviewStartResponse(null, 0, 0, Collections.emptyList(), null)));
      }

      Collections.shuffle(wordsToRepeat);

      // Utwórz sesję review
      String sessionId = generateSessionId(userEmail);
      ReviewSession session = new ReviewSession();
      session.setUserId(userId);
      session.setWordsToRepeat(new ArrayList<>(wordsToRepeat));
      session.setCorrectWordOnView(0);
      session.setCurrentWordIndex(0);

      // Oblicz początkową liczbę poprawnych słów (pierwsze 8)
      int initialCorrectCount = 0;
      int wordsToShow = Math.min(8, wordsToRepeat.size());
      for (int i = 0; i < wordsToShow; i++) {
        if (wordsToRepeat.get(i).isCorrect()) {
          initialCorrectCount++;
        }
      }
      session.setCorrectWordOnView(initialCorrectCount);

      reviewSessions.put(sessionId, session);

      // Przygotuj odpowiedź
      List<WordToRepeatResponse> initialWords =
          wordsToRepeat.stream().limit(8).map(this::mapToWordToRepeatResponse).toList();

      WordToRepeatResponse currentWord = mapToWordToRepeatResponse(wordsToRepeat.get(0));

      ReviewStartResponse response =
          new ReviewStartResponse(
              sessionId, wordsToRepeat.size(), initialCorrectCount, initialWords, currentWord);

      logger.info(
          "Review session started for user: {} with {} words", userEmail, wordsToRepeat.size());
      return ResponseEntity.ok(ApiResponse.success("Review session started", response));

    } catch (Exception e) {
      logger.error(
          "Error starting review session for user: {}",
          authentication != null ? authentication.getName() : "unknown",
          e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Failed to start review session"));
    }
  }

  @PostMapping("/{sessionId}/next-word")
  public ResponseEntity<ApiResponse<NextWordResponse>> getNextWord(
      @PathVariable String sessionId, Authentication authentication) {
    try {
      if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("User not authenticated"));
      }

      String userEmail = authentication.getName();

      ReviewSession session = reviewSessions.get(sessionId);
      if (session == null) {
        return ResponseEntity.badRequest().body(ApiResponse.error("Invalid review session"));
      }

      // Sprawdź czy to sesja tego użytkownika
      if (!sessionId.contains(userEmail.hashCode() + "")) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error("Access denied to session"));
      }

      if (session.getWordsToRepeat().isEmpty()) {
        return ResponseEntity.badRequest().body(ApiResponse.error("No words available in session"));
      }

      // Tasuj słowa i pobierz pierwsze
      Collections.shuffle(session.getWordsToRepeat());
      WordToRepeatDto nextWord = session.getWordsToRepeat().get(0);
      session.setSystemAddCorrectWord(nextWord.isCorrect());

      WordToRepeatResponse wordResponse = mapToWordToRepeatResponse(nextWord);
      NextWordResponse response =
          new NextWordResponse(
              wordResponse, session.getCorrectWordOnView(), session.getWordsToRepeat().size());

      logger.debug("Next word provided for session: {}", sessionId);
      return ResponseEntity.ok(ApiResponse.success("Next word retrieved", response));

    } catch (Exception e) {
      logger.error("Error getting next word for session: {}", sessionId, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Failed to get next word"));
    }
  }

  @PostMapping("/{sessionId}/check-answer")
  public ResponseEntity<ApiResponse<CheckAnswerResponse>> checkAnswer(
      @PathVariable String sessionId,
      @Valid @RequestBody CheckAnswerRequest request,
      Authentication authentication) {
    try {
      if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("User not authenticated"));
      }

      String userEmail = authentication.getName();

      ReviewSession session = reviewSessions.get(sessionId);
      if (session == null) {
        return ResponseEntity.badRequest().body(ApiResponse.error("Invalid review session"));
      }

      if (!sessionId.contains(userEmail.hashCode() + "")) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error("Access denied to session"));
      }

      WordToRepeatDto wordToCheck =
          session.getWordsToRepeat().stream()
              .filter(
                  w ->
                      w.word().equals(request.word())
                          && w.translation().equals(request.translation()))
              .findFirst()
              .orElse(null);

      if (wordToCheck == null) {
        return ResponseEntity.badRequest()
            .body(ApiResponse.error("Word not found in current session"));
      }

      boolean wasUserCorrect = request.userCorrect();
      int newCorrectCount = session.getCorrectWordOnView();

      if (!wordToCheck.isCorrect() && wasUserCorrect) {
        if (session.isSystemAddCorrectWord()) {
          newCorrectCount++;
        }
      } else if (wordToCheck.isCorrect() && !wasUserCorrect && !session.isSystemAddCorrectWord()) {
        newCorrectCount--;
      }

      session.setCorrectWordOnView(Math.max(0, newCorrectCount));

      CheckAnswerResponse response =
          new CheckAnswerResponse(
              wasUserCorrect,
              wordToCheck.isCorrect(),
              session.getCorrectWordOnView(),
              session.getWordsToRepeat().size());

      logger.debug(
          "Answer checked for session: {} - user correct: {}, word correct: {}",
          sessionId,
          wasUserCorrect,
          wordToCheck.isCorrect());

      return ResponseEntity.ok(ApiResponse.success("Answer checked", response));

    } catch (Exception e) {
      logger.error("Error checking answer for session: {}", sessionId, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Failed to check answer"));
    }
  }

  @GetMapping("/{sessionId}/status")
  public ResponseEntity<ApiResponse<ReviewStatusResponse>> getReviewStatus(
      @PathVariable String sessionId, Authentication authentication) {
    try {
      if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("User not authenticated"));
      }

      String userEmail = authentication.getName();

      ReviewSession session = reviewSessions.get(sessionId);
      if (session == null) {
        return ResponseEntity.badRequest().body(ApiResponse.error("Invalid review session"));
      }

      if (!sessionId.contains(userEmail.hashCode() + "")) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error("Access denied to session"));
      }

      ReviewStatusResponse response =
          new ReviewStatusResponse(
              session.getCorrectWordOnView(), session.getWordsToRepeat().size(), sessionId);

      return ResponseEntity.ok(ApiResponse.success("Review status retrieved", response));

    } catch (Exception e) {
      logger.error("Error getting review status for session: {}", sessionId, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Failed to get review status"));
    }
  }

  @DeleteMapping("/{sessionId}")
  public ResponseEntity<ApiResponse<String>> endReviewSession(
      @PathVariable String sessionId, Authentication authentication) {
    try {
      if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("User not authenticated"));
      }

      String userEmail = authentication.getName();

      if (!sessionId.contains(userEmail.hashCode() + "")) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error("Access denied to session"));
      }

      ReviewSession removedSession = reviewSessions.remove(sessionId);
      if (removedSession == null) {
        return ResponseEntity.badRequest().body(ApiResponse.error("Session not found"));
      }

      logger.info("Review session ended for user: {}", userEmail);
      return ResponseEntity.ok(ApiResponse.success("Review session ended", "Session closed"));

    } catch (Exception e) {
      logger.error("Error ending review session: {}", sessionId, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Failed to end review session"));
    }
  }

  private String generateSessionId(String userEmail) {
    return "review_" + userEmail.hashCode() + "_" + System.currentTimeMillis();
  }

  private WordToRepeatResponse mapToWordToRepeatResponse(WordToRepeatDto dto) {
    return new WordToRepeatResponse(dto.word(), dto.translation(), dto.isCorrect());
  }

  // Session class
  private static class ReviewSession {
    private Long userId;
    private List<WordToRepeatDto> wordsToRepeat;
    private int correctWordOnView;
    private int currentWordIndex;
    private boolean systemAddCorrectWord;

    // Getters and setters
    public Long getUserId() {
      return userId;
    }

    public void setUserId(Long userId) {
      this.userId = userId;
    }

    public List<WordToRepeatDto> getWordsToRepeat() {
      return wordsToRepeat;
    }

    public void setWordsToRepeat(List<WordToRepeatDto> wordsToRepeat) {
      this.wordsToRepeat = wordsToRepeat;
    }

    public int getCorrectWordOnView() {
      return correctWordOnView;
    }

    public void setCorrectWordOnView(int correctWordOnView) {
      this.correctWordOnView = correctWordOnView;
    }

    public int getCurrentWordIndex() {
      return currentWordIndex;
    }

    public void setCurrentWordIndex(int currentWordIndex) {
      this.currentWordIndex = currentWordIndex;
    }

    public boolean isSystemAddCorrectWord() {
      return systemAddCorrectWord;
    }

    public void setSystemAddCorrectWord(boolean systemAddCorrectWord) {
      this.systemAddCorrectWord = systemAddCorrectWord;
    }
  }
}
