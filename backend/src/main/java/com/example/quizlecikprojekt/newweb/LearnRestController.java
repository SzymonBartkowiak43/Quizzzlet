package com.example.quizlecikprojekt.newweb;

import com.example.quizlecikprojekt.domain.word.Word;
import com.example.quizlecikprojekt.domain.word.WordService;
import com.example.quizlecikprojekt.domain.wordset.WordSetService;
import com.example.quizlecikprojekt.newweb.dto.*;
import com.example.quizlecikprojekt.newweb.dto.test.TestAnswerResult;
import com.example.quizlecikprojekt.newweb.dto.test.TestResultResponse;
import com.example.quizlecikprojekt.newweb.dto.test.TestStartResponse;
import com.example.quizlecikprojekt.newweb.dto.test.TestSubmissionRequest;
import com.example.quizlecikprojekt.newweb.dto.word.WordResponse;
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
@RequestMapping("/api/wordsets")
@CrossOrigin(origins = "http://localhost:3000")
public class LearnRestController {

  private static final Logger logger = LoggerFactory.getLogger(LearnRestController.class);

  private final WordSetService wordSetService;
  private final WordService wordService;

  private final Map<String, FlashCardSession> flashCardSessions = new ConcurrentHashMap<>();

  public LearnRestController(WordSetService wordSetService, WordService wordService) {
    this.wordSetService = wordSetService;
    this.wordService = wordService;
  }

  @PostMapping("/{id}/flashcards/start")
  public ResponseEntity<ApiResponse<FlashCardStartResponse>> startFlashCards(
      @PathVariable Long id, Authentication authentication) {
    try {
      if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("User not authenticated"));
      }

      String userEmail = authentication.getName();

      if (!wordSetService.isWordSetOwnedByUser(id, userEmail)) { // Poprawiona logika!
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Access denied"));
      }

      List<Word> words = wordSetService.getWordsByWordSetId(id);
      if (words.isEmpty()) {
        return ResponseEntity.badRequest().body(ApiResponse.error("Word set is empty"));
      }

      Collections.shuffle(words);

      String sessionId = generateSessionId(userEmail, id);
      FlashCardSession session = new FlashCardSession();
      session.setWordSetId(id);
      session.setWords(new ArrayList<>(words));
      session.setCurrentIndex(0);
      session.setScore(0);
      session.setUncorrectedWords(new ArrayList<>());

      flashCardSessions.put(sessionId, session);

      FlashCardStartResponse response =
          new FlashCardStartResponse(
              sessionId, words.size(), 0, mapToWordResponse(words.getFirst()));

      logger.info("FlashCard session started for user: {} on wordSet: {}", userEmail, id);
      return ResponseEntity.ok(ApiResponse.success("FlashCard session started", response));

    } catch (Exception e) {
      logger.error("Error starting flashcard session for wordSet: {}", id, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Failed to start flashcard session"));
    }
  }

  @PostMapping("/{id}/flashcards/{sessionId}/answer")
  public ResponseEntity<ApiResponse<FlashCardAnswerResponse>> answerFlashCard(
      @PathVariable Long id,
      @PathVariable String sessionId,
      @Valid @RequestBody FlashCardAnswerRequest request,
      Authentication authentication) {
    try {
      if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("User not authenticated"));
      }

      String userEmail = authentication.getName();

      FlashCardSession session = flashCardSessions.get(sessionId);
      if (session == null || !session.getWordSetId().equals(id)) {
        return ResponseEntity.badRequest().body(ApiResponse.error("Invalid session"));
      }

      if (!sessionId.contains(userEmail.hashCode() + "")) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error("Access denied to session"));
      }

      if (session.getCurrentIndex() >= session.getWords().size()) {
        return ResponseEntity.badRequest().body(ApiResponse.error("Session already completed"));
      }

      Word currentWord = session.getWords().get(session.getCurrentIndex());

      if (!currentWord.getId().equals(request.wordId())) {
        return ResponseEntity.badRequest().body(ApiResponse.error("Word ID mismatch"));
      }

      if (request.correct()) {
        currentWord.addPoint();
        session.setScore(session.getScore() + 1);
        logger.debug("User {} answered correctly for word: {}", userEmail, currentWord.getId());
      } else {
        currentWord.subtractPoint();
        session.getUncorrectedWords().add(currentWord);
        logger.debug("User {} answered incorrectly for word: {}", userEmail, currentWord.getId());
      }

      wordService.saveWord(currentWord);

      session.setCurrentIndex(session.getCurrentIndex() + 1);

      FlashCardAnswerResponse response;

      if (session.getCurrentIndex() >= session.getWords().size()) {
        response =
            new FlashCardAnswerResponse(
                session.getScore(),
                session.getCurrentIndex(),
                session.getWords().size(),
                true,
                null, // nextWord
                session.getUncorrectedWords().stream().map(this::mapToWordResponse).toList());

        flashCardSessions.remove(sessionId);

        logger.info(
            "FlashCard session completed for user: {} on wordSet: {} with score: {}/{}",
            userEmail,
            id,
            session.getScore(),
            session.getWords().size());
      } else {
        response =
            new FlashCardAnswerResponse(
                session.getScore(),
                session.getCurrentIndex(),
                session.getWords().size(),
                false,
                mapToWordResponse(session.getWords().get(session.getCurrentIndex())),
                null // uncorrectedWords
                );
      }

      return ResponseEntity.ok(ApiResponse.success("Answer processed", response));

    } catch (Exception e) {
      logger.error("Error processing flashcard answer for session: {}", sessionId, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Failed to process answer"));
    }
  }

  @PostMapping("/{id}/test/start")
  public ResponseEntity<ApiResponse<TestStartResponse>> startTest(
      @PathVariable Long id, Authentication authentication) {
    try {
      if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("User not authenticated"));
      }

      String userEmail = authentication.getName();

      if (!wordSetService.isWordSetOwnedByUser(id, userEmail)) { // Poprawiona logika!
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Access denied"));
      }

      List<Word> words = wordSetService.getWordsByWordSetId(id);
      if (words.size() < 4) {
        return ResponseEntity.badRequest()
            .body(ApiResponse.error("Word set must have at least 4 words to start test"));
      }

      Collections.shuffle(words);

      TestStartResponse response =
          new TestStartResponse(
              id, words.stream().map(this::mapToWordResponse).toList(), words.size());

      logger.info("Test started for user: {} on wordSet: {}", userEmail, id);
      return ResponseEntity.ok(ApiResponse.success("Test started", response));

    } catch (Exception e) {
      logger.error("Error starting test for wordSet: {}", id, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Failed to start test"));
    }
  }

  @PostMapping("/{id}/test/submit")
  public ResponseEntity<ApiResponse<TestResultResponse>> submitTest(
      @PathVariable Long id,
      @Valid @RequestBody TestSubmissionRequest request,
      Authentication authentication) {
    try {
      if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("User not authenticated"));
      }

      String userEmail = authentication.getName();

      if (!wordSetService.isWordSetOwnedByUser(id, userEmail)) { // Poprawiona logika!
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Access denied"));
      }

      List<Word> words = wordSetService.getWordsByWordSetId(id);
      Map<Long, String> userAnswers = request.answers();

      List<TestAnswerResult> results = new ArrayList<>();
      List<WordResponse> correctWords = new ArrayList<>();
      List<WordResponse> incorrectWords = new ArrayList<>();
      int score = 0;

      for (Word word : words) {
        String userAnswer = userAnswers.get(word.getId());
        boolean isCorrect = false;

        if (userAnswer != null
            && userAnswer.trim().equalsIgnoreCase(word.getTranslation().trim())) {
          isCorrect = true;
          score++;
          word.addPoint();
          correctWords.add(mapToWordResponse(word));
        } else {
          word.subtractPoint();
          incorrectWords.add(mapToWordResponse(word));
        }

        wordService.saveWord(word);

        TestAnswerResult result =
            new TestAnswerResult(
                word.getId(),
                word.getWord(),
                word.getTranslation(),
                userAnswer != null ? userAnswer : "",
                isCorrect);
        results.add(result);
      }

      TestResultResponse response =
          new TestResultResponse(
              score,
              words.size(),
              (score * 100.0) / words.size(),
              results,
              correctWords,
              incorrectWords);

      logger.info(
          "Test completed for user: {} on wordSet: {} with score: {}/{}",
          userEmail,
          id,
          score,
          words.size());

      return ResponseEntity.ok(ApiResponse.success("Test completed", response));

    } catch (Exception e) {
      logger.error("Error submitting test for wordSet: {}", id, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Failed to submit test"));
    }
  }

  private String generateSessionId(String userEmail, Long wordSetId) {
    return "fc_" + userEmail.hashCode() + "_" + wordSetId + "_" + System.currentTimeMillis();
  }

  private WordResponse mapToWordResponse(Word word) {
    return new WordResponse(
        word.getId(),
        word.getWord(),
        word.getTranslation(),
        word.getPoints(),
        word.getLastPracticed());
  }

  private static class FlashCardSession {
    private Long wordSetId;
    private List<Word> words;
    private int currentIndex;
    private int score;
    private List<Word> uncorrectedWords;

    public Long getWordSetId() {
      return wordSetId;
    }

    public void setWordSetId(Long wordSetId) {
      this.wordSetId = wordSetId;
    }

    public List<Word> getWords() {
      return words;
    }

    public void setWords(List<Word> words) {
      this.words = words;
    }

    public int getCurrentIndex() {
      return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
      this.currentIndex = currentIndex;
    }

    public int getScore() {
      return score;
    }

    public void setScore(int score) {
      this.score = score;
    }

    public List<Word> getUncorrectedWords() {
      return uncorrectedWords;
    }

    public void setUncorrectedWords(List<Word> uncorrectedWords) {
      this.uncorrectedWords = uncorrectedWords;
    }
  }
}
