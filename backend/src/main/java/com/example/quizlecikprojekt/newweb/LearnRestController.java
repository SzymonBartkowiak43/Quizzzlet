package com.example.quizlecikprojekt.newweb;

import com.example.quizlecikprojekt.domain.word.Word;
import com.example.quizlecikprojekt.domain.word.WordService;
import com.example.quizlecikprojekt.domain.wordSet.WordSetService;
import com.example.quizlecikprojekt.newweb.dto.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/wordsets")
@CrossOrigin(origins = "http://localhost:3000")
public class LearnRestController {

    private static final Logger logger = LoggerFactory.getLogger(LearnRestController.class);

    private final WordSetService wordSetService;
    private final WordService wordService;

    // Session storage dla aktywnych sesji uczenia - w produkcji lepiej użyć Redis
    private final Map<String, FlashCardSession> flashCardSessions = new ConcurrentHashMap<>();

    public LearnRestController(WordSetService wordSetService, WordService wordService) {
        this.wordSetService = wordSetService;
        this.wordService = wordService;
    }

    // === FLASHCARDS ENDPOINTS ===

    @PostMapping("/{id}/flashcards/start")
    public ResponseEntity<ApiResponse<FlashCardStartResponse>> startFlashCards(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not authenticated"));
            }

            String userEmail = authentication.getName();

            // Sprawdź dostęp do WordSet
            if (!wordSetService.isWordSetOwnedByUser(id, userEmail)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("Access denied"));
            }

            List<Word> words = wordSetService.getWordsByWordSetId(id);
            if (words.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Word set is empty"));
            }

            // Tasuj słowa
            Collections.shuffle(words);

            // Utwórz sesję
            String sessionId = generateSessionId(userEmail, id);
            FlashCardSession session = new FlashCardSession();
            session.setWordSetId(id);
            session.setWords(new ArrayList<>(words));
            session.setCurrentIndex(0);
            session.setScore(0);
            session.setUncorrectedWords(new ArrayList<>());

            flashCardSessions.put(sessionId, session);

            FlashCardStartResponse response = new FlashCardStartResponse();
            response.setSessionId(sessionId);
            response.setTotalWords(words.size());
            response.setCurrentWord(mapToWordResponse(words.get(0)));
            response.setCurrentIndex(0);

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

            // Sprawdź sesję
            FlashCardSession session = flashCardSessions.get(sessionId);
            if (session == null || !session.getWordSetId().equals(id)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid session"));
            }

            // Sprawdź czy to właściwa sesja użytkownika
            if (!sessionId.contains(userEmail.hashCode() + "")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("Access denied to session"));
            }

            if (session.getCurrentIndex() >= session.getWords().size()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Session already completed"));
            }

            Word currentWord = session.getWords().get(session.getCurrentIndex());

            // Sprawdź czy ID słowa się zgadza
            if (!currentWord.getId().equals(request.getWordId())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Word ID mismatch"));
            }

            // Przetwórz odpowiedź
            if (request.isCorrect()) {
                currentWord.addPoint();
                session.setScore(session.getScore() + 1);
                logger.debug("User {} answered correctly for word: {}", userEmail, currentWord.getId());
            } else {
                currentWord.subtractPoint();
                session.getUncorrectedWords().add(currentWord);
                logger.debug("User {} answered incorrectly for word: {}", userEmail, currentWord.getId());
            }

            // Zapisz zmiany w słowie
            wordService.saveWord(currentWord);

            // Przejdź do następnego słowa
            session.setCurrentIndex(session.getCurrentIndex() + 1);

            FlashCardAnswerResponse response = new FlashCardAnswerResponse();
            response.setScore(session.getScore());
            response.setCurrentIndex(session.getCurrentIndex());
            response.setTotalWords(session.getWords().size());

            // Sprawdź czy to koniec sesji
            if (session.getCurrentIndex() >= session.getWords().size()) {
                response.setCompleted(true);
                response.setUncorrectedWords(session.getUncorrectedWords().stream()
                        .map(this::mapToWordResponse)
                        .toList());

                // Usuń sesję po zakończeniu
                flashCardSessions.remove(sessionId);

                logger.info("FlashCard session completed for user: {} on wordSet: {} with score: {}/{}",
                        userEmail, id, session.getScore(), session.getWords().size());
            } else {
                response.setCompleted(false);
                response.setNextWord(mapToWordResponse(session.getWords().get(session.getCurrentIndex())));
            }

            return ResponseEntity.ok(ApiResponse.success("Answer processed", response));

        } catch (Exception e) {
            logger.error("Error processing flashcard answer for session: {}", sessionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to process answer"));
        }
    }

    // === TEST ENDPOINTS ===

    @PostMapping("/{id}/test/start")
    public ResponseEntity<ApiResponse<TestStartResponse>> startTest(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not authenticated"));
            }

            String userEmail = authentication.getName();

            if (!wordSetService.isWordSetOwnedByUser(id, userEmail)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("Access denied"));
            }

            List<Word> words = wordSetService.getWordsByWordSetId(id);
            if (words.size() < 4) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Word set must have at least 4 words to start test"));
            }

            Collections.shuffle(words);

            TestStartResponse response = new TestStartResponse();
            response.setWordSetId(id);
            response.setWords(words.stream().map(this::mapToWordResponse).toList());
            response.setTotalQuestions(words.size());

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

            if (!wordSetService.isWordSetOwnedByUser(id, userEmail)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("Access denied"));
            }

            List<Word> words = wordSetService.getWordsByWordSetId(id);
            Map<Long, String> userAnswers = request.getAnswers();

            List<TestAnswerResult> results = new ArrayList<>();
            List<Word> correctWords = new ArrayList<>();
            List<Word> incorrectWords = new ArrayList<>();
            int score = 0;

            for (Word word : words) {
                String userAnswer = userAnswers.get(word.getId());
                boolean isCorrect = false;

                if (userAnswer != null &&
                        userAnswer.trim().equalsIgnoreCase(word.getTranslation().trim())) {
                    isCorrect = true;
                    score++;
                    word.addPoint();
                    correctWords.add(word);
                } else {
                    word.subtractPoint();
                    incorrectWords.add(word);
                }

                wordService.saveWord(word);

                TestAnswerResult result = new TestAnswerResult();
                result.setWordId(word.getId());
                result.setWord(word.getWord());
                result.setCorrectAnswer(word.getTranslation());
                result.setUserAnswer(userAnswer != null ? userAnswer : "");
                result.setCorrect(isCorrect);
                results.add(result);
            }

            TestResultResponse response = new TestResultResponse();
            response.setScore(score);
            response.setTotalQuestions(words.size());
            response.setPercentage((score * 100.0) / words.size());
            response.setResults(results);
            response.setCorrectWords(correctWords.stream().map(this::mapToWordResponse).toList());
            response.setIncorrectWords(incorrectWords.stream().map(this::mapToWordResponse).toList());

            logger.info("Test completed for user: {} on wordSet: {} with score: {}/{}",
                    userEmail, id, score, words.size());

            return ResponseEntity.ok(ApiResponse.success("Test completed", response));

        } catch (Exception e) {
            logger.error("Error submitting test for wordSet: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to submit test"));
        }
    }

    // === HELPER METHODS ===

    private String generateSessionId(String userEmail, Long wordSetId) {
        return "fc_" + userEmail.hashCode() + "_" + wordSetId + "_" + System.currentTimeMillis();
    }

    private WordResponse mapToWordResponse(Word word) {
        WordResponse response = new WordResponse();
        response.setId(word.getId());
        response.setWord(word.getWord());
        response.setTranslation(word.getTranslation());
        response.setPoints(word.getPoints());
        response.setLastPracticed(word.getLastPracticed());
        return response;
    }

    // === SESSION CLASS ===
    private static class FlashCardSession {
        private Long wordSetId;
        private List<Word> words;
        private int currentIndex;
        private int score;
        private List<Word> uncorrectedWords;

        // Getters and setters
        public Long getWordSetId() { return wordSetId; }
        public void setWordSetId(Long wordSetId) { this.wordSetId = wordSetId; }

        public List<Word> getWords() { return words; }
        public void setWords(List<Word> words) { this.words = words; }

        public int getCurrentIndex() { return currentIndex; }
        public void setCurrentIndex(int currentIndex) { this.currentIndex = currentIndex; }

        public int getScore() { return score; }
        public void setScore(int score) { this.score = score; }

        public List<Word> getUncorrectedWords() { return uncorrectedWords; }
        public void setUncorrectedWords(List<Word> uncorrectedWords) { this.uncorrectedWords = uncorrectedWords; }
    }
}