package com.example.quizlecikprojekt.controllers;

import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserService;
import com.example.quizlecikprojekt.domain.word.Word;
import com.example.quizlecikprojekt.domain.word.WordService;
import com.example.quizlecikprojekt.domain.wordset.WordSet;
import com.example.quizlecikprojekt.domain.wordset.WordSetService;
import com.example.quizlecikprojekt.controllers.dto.learn.*;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/learn")
@CrossOrigin(origins = "http://localhost:3000")
public class LearnRestController {

  private final WordSetService wordSetService;
  private final WordService wordService;
  private final UserService userService;

  private final Map<String, FlashcardSession> flashcardSessions = new ConcurrentHashMap<>();
  private final Map<String, QuizSession> quizSessions = new ConcurrentHashMap<>();

  public LearnRestController(
      WordSetService wordSetService, WordService wordService, UserService userService) {
    this.wordSetService = wordSetService;
    this.wordService = wordService;
    this.userService = userService;
  }

  // Flashcard endpoints
  @PostMapping("/flashcards/start")
  public ResponseEntity<?> startFlashcardSession(
      Authentication authentication, @Valid @RequestBody StartFlashcardRequest request) {

    User user = userService.getUserByEmail(authentication.getName());

    // Verify user owns the word set
    if (wordSetService.isWordSetOwnedByUser(request.wordSetId(), user.getEmail())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(Map.of("message", "You don't have permission to access this word set"));
    }

    WordSet wordSet = wordSetService.getWordSetById(request.wordSetId());
    List<Word> words = wordSetService.getWordsByWordSetId(request.wordSetId());

    if (words.isEmpty()) {
      return ResponseEntity.badRequest().body(Map.of("message", "Word set has no words to learn"));
    }

    // Shuffle words for variety
    Collections.shuffle(words);

    String sessionId = generateSessionId();
    FlashcardSession session = new FlashcardSession();
    session.setSessionId(sessionId);
    session.setWordSetId(request.wordSetId());
    session.setWordSetTitle(wordSet.getTitle());
    session.setWords(words);
    session.setCurrentIndex(0);
    session.setScore(0);
    session.setIncorrectWords(new ArrayList<>());
    session.setStartedAt(LocalDateTime.now());

    flashcardSessions.put(sessionId, session);

    FlashcardSessionResponse response = buildFlashcardResponse(session);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/flashcards/answer")
  public ResponseEntity<?> answerFlashcard(
      Authentication authentication, @Valid @RequestBody AnswerFlashcardRequest request) {

    FlashcardSession session = flashcardSessions.get(request.sessionId());
    if (session == null) {
      return ResponseEntity.notFound().build();
    }

    Word currentWord = session.getWords().get(session.getCurrentIndex());

    if (request.isCorrect()) {
      session.incrementScore();
      // Update word points in database
      wordService.updateWordPoints(currentWord.getId(), currentWord.getPoints() + 1);
    } else {
      session.addIncorrectWord(currentWord);
    }

    // Move to next word
    session.setCurrentIndex(session.getCurrentIndex() + 1);

    FlashcardSessionResponse response = buildFlashcardResponse(session);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/flashcards/{sessionId}")
  public ResponseEntity<?> getFlashcardSession(@PathVariable String sessionId) {
    FlashcardSession session = flashcardSessions.get(sessionId);
    if (session == null) {
      return ResponseEntity.notFound().build();
    }

    FlashcardSessionResponse response = buildFlashcardResponse(session);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/flashcards/{sessionId}")
  public ResponseEntity<?> endFlashcardSession(@PathVariable String sessionId) {
    FlashcardSession session = flashcardSessions.remove(sessionId);
    if (session == null) {
      return ResponseEntity.notFound().build();
    }

    SessionSummaryResponse summary = buildSessionSummary(session, "flashcard");
    return ResponseEntity.ok(summary);
  }

  // Quiz endpoints
  @PostMapping("/quiz/start")
  public ResponseEntity<?> startQuizSession(
      Authentication authentication, @Valid @RequestBody StartQuizRequest request) {

    User user = userService.getUserByEmail(authentication.getName());

    if (wordSetService.isWordSetOwnedByUser(request.wordSetId(), user.getEmail())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(Map.of("message", "You don't have permission to access this word set"));
    }

    WordSet wordSet = wordSetService.getWordSetById(request.wordSetId());
    List<Word> allWords = wordSetService.getWordsByWordSetId(request.wordSetId());

    if (allWords.isEmpty()) {
      return ResponseEntity.badRequest().body(Map.of("message", "Word set has no words for quiz"));
    }

    // Limit questions if specified
    List<Word> quizWords = allWords;
    if (request.numberOfQuestions() > 0 && request.numberOfQuestions() < allWords.size()) {
      Collections.shuffle(allWords);
      quizWords = allWords.subList(0, request.numberOfQuestions());
    }

    String sessionId = generateSessionId();
    QuizSession session = new QuizSession();
    session.setSessionId(sessionId);
    session.setWordSetId(request.wordSetId());
    session.setWordSetTitle(wordSet.getTitle());
    session.setQuestions(generateQuizQuestions(quizWords, allWords));
    session.setCurrentQuestionIndex(0);
    session.setScore(0);
    session.setIncorrectWords(new ArrayList<>());
    session.setStartedAt(LocalDateTime.now());

    quizSessions.put(sessionId, session);

    QuizSessionResponse response = buildQuizResponse(session);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/quiz/answer")
  public ResponseEntity<?> answerQuiz(
      Authentication authentication, @Valid @RequestBody AnswerQuizRequest request) {

    QuizSession session = quizSessions.get(request.sessionId());
    if (session == null) {
      return ResponseEntity.notFound().build();
    }

    QuizQuestion currentQuestion = session.getQuestions().get(session.getCurrentQuestionIndex());
    boolean isCorrect =
        currentQuestion.getCorrectAnswer().equalsIgnoreCase(request.answer().trim());

    if (isCorrect) {
      session.incrementScore();
      // Update word points
      wordService.updateWordPoints(
          currentQuestion.getWord().getId(), currentQuestion.getWord().getPoints() + 1);
    } else {
      session.addIncorrectWord(currentQuestion.getWord());
    }

    session.setCurrentQuestionIndex(session.getCurrentQuestionIndex() + 1);

    QuizSessionResponse response = buildQuizResponse(session);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/quiz/{sessionId}")
  public ResponseEntity<?> getQuizSession(@PathVariable String sessionId) {
    QuizSession session = quizSessions.get(sessionId);
    if (session == null) {
      return ResponseEntity.notFound().build();
    }

    QuizSessionResponse response = buildQuizResponse(session);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/quiz/{sessionId}")
  public ResponseEntity<?> endQuizSession(@PathVariable String sessionId) {
    QuizSession session = quizSessions.remove(sessionId);
    if (session == null) {
      return ResponseEntity.notFound().build();
    }

    SessionSummaryResponse summary = buildSessionSummary(session, "quiz");
    return ResponseEntity.ok(summary);
  }

  // Helper methods
  private String generateSessionId() {
    return UUID.randomUUID().toString();
  }

  private FlashcardSessionResponse buildFlashcardResponse(FlashcardSession session) {
    boolean isCompleted = session.getCurrentIndex() >= session.getWords().size();
    FlashcardResponse currentCard = null;

    if (!isCompleted) {
      Word currentWord = session.getWords().get(session.getCurrentIndex());
      currentCard =
          new FlashcardResponse(
              currentWord.getId(), currentWord.getWord(), currentWord.getTranslation(), false);
    }

    return new FlashcardSessionResponse(
        session.getSessionId(),
        session.getWordSetId(),
        session.getWordSetTitle(),
        session.getWords().size(),
        session.getCurrentIndex(),
        session.getScore(),
        isCompleted,
        currentCard);
  }

  private List<QuizQuestion> generateQuizQuestions(List<Word> quizWords, List<Word> allWords) {
    List<QuizQuestion> questions = new ArrayList<>();
    Random random = new Random();

    for (Word word : quizWords) {
      // Randomly choose question type
      boolean wordToTranslation = random.nextBoolean();

      QuizQuestion question = new QuizQuestion();
      question.setWord(word);

      if (wordToTranslation) {
        question.setQuestion(word.getWord());
        question.setCorrectAnswer(word.getTranslation());
        question.setQuestionType("word_to_translation");
        question.setOptions(generateOptions(word.getTranslation(), allWords, false));
      } else {
        question.setQuestion(word.getTranslation());
        question.setCorrectAnswer(word.getWord());
        question.setQuestionType("translation_to_word");
        question.setOptions(generateOptions(word.getWord(), allWords, true));
      }

      questions.add(question);
    }

    Collections.shuffle(questions);
    return questions;
  }

  private List<String> generateOptions(
      String correctAnswer, List<Word> allWords, boolean useWords) {
    Set<String> options = new HashSet<>();
    options.add(correctAnswer);

    List<String> candidates =
        allWords.stream()
            .map(useWords ? Word::getWord : Word::getTranslation)
            .filter(s -> !s.equals(correctAnswer))
            .collect(Collectors.toList());

    Collections.shuffle(candidates);

    // Add 3 incorrect options
    for (int i = 0; i < Math.min(3, candidates.size()); i++) {
      options.add(candidates.get(i));
    }

    List<String> result = new ArrayList<>(options);
    Collections.shuffle(result);
    return result;
  }

  private QuizSessionResponse buildQuizResponse(QuizSession session) {
    boolean isCompleted = session.getCurrentQuestionIndex() >= session.getQuestions().size();
    QuizQuestionResponse currentQuestion = null;

    if (!isCompleted) {
      QuizQuestion question = session.getQuestions().get(session.getCurrentQuestionIndex());
      currentQuestion =
          new QuizQuestionResponse(
              question.getWord().getId(),
              question.getQuestion(),
              question.getOptions(),
              question.getQuestionType());
    }

    return new QuizSessionResponse(
        session.getSessionId(),
        session.getWordSetId(),
        session.getWordSetTitle(),
        session.getQuestions().size(),
        session.getCurrentQuestionIndex(),
        session.getScore(),
        isCompleted,
        currentQuestion);
  }

  private SessionSummaryResponse buildSessionSummary(Object session, String type) {
    if (session instanceof FlashcardSession fs) {
      double accuracy =
          fs.getWords().size() > 0 ? (double) fs.getScore() / fs.getWords().size() * 100 : 0;

      List<WordResultResponse> incorrectWords =
          fs.getIncorrectWords().stream()
              .map(
                  word ->
                      new WordResultResponse(
                          word.getId(), word.getWord(), word.getTranslation(), false))
              .toList();

      return new SessionSummaryResponse(
          fs.getSessionId(),
          type,
          fs.getWordSetId(),
          fs.getWordSetTitle(),
          fs.getWords().size(),
          fs.getScore(),
          fs.getWords().size() - fs.getScore(),
          accuracy,
          incorrectWords,
          LocalDateTime.now().toString());
    } else if (session instanceof QuizSession qs) {
      double accuracy =
          qs.getQuestions().size() > 0
              ? (double) qs.getScore() / qs.getQuestions().size() * 100
              : 0;

      List<WordResultResponse> incorrectWords =
          qs.getIncorrectWords().stream()
              .map(
                  word ->
                      new WordResultResponse(
                          word.getId(), word.getWord(), word.getTranslation(), false))
              .toList();

      return new SessionSummaryResponse(
          qs.getSessionId(),
          type,
          qs.getWordSetId(),
          qs.getWordSetTitle(),
          qs.getQuestions().size(),
          qs.getScore(),
          qs.getQuestions().size() - qs.getScore(),
          accuracy,
          incorrectWords,
          LocalDateTime.now().toString());
    }

    throw new IllegalArgumentException("Unknown session type");
  }

  // Session classes
  private static class FlashcardSession {
    private String sessionId;
    private Long wordSetId;
    private String wordSetTitle;
    private List<Word> words;
    private int currentIndex;
    private int score;
    private List<Word> incorrectWords;
    private LocalDateTime startedAt;

    // Getters and setters
    public String getSessionId() {
      return sessionId;
    }

    public void setSessionId(String sessionId) {
      this.sessionId = sessionId;
    }

    public Long getWordSetId() {
      return wordSetId;
    }

    public void setWordSetId(Long wordSetId) {
      this.wordSetId = wordSetId;
    }

    public String getWordSetTitle() {
      return wordSetTitle;
    }

    public void setWordSetTitle(String wordSetTitle) {
      this.wordSetTitle = wordSetTitle;
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

    public void incrementScore() {
      this.score++;
    }

    public List<Word> getIncorrectWords() {
      return incorrectWords;
    }

    public void setIncorrectWords(List<Word> incorrectWords) {
      this.incorrectWords = incorrectWords;
    }

    public void addIncorrectWord(Word word) {
      this.incorrectWords.add(word);
    }

    public LocalDateTime getStartedAt() {
      return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
      this.startedAt = startedAt;
    }
  }

  private static class QuizSession {
    private String sessionId;
    private Long wordSetId;
    private String wordSetTitle;
    private List<QuizQuestion> questions;
    private int currentQuestionIndex;
    private int score;
    private List<Word> incorrectWords;
    private LocalDateTime startedAt;

    // Getters and setters
    public String getSessionId() {
      return sessionId;
    }

    public void setSessionId(String sessionId) {
      this.sessionId = sessionId;
    }

    public Long getWordSetId() {
      return wordSetId;
    }

    public void setWordSetId(Long wordSetId) {
      this.wordSetId = wordSetId;
    }

    public String getWordSetTitle() {
      return wordSetTitle;
    }

    public void setWordSetTitle(String wordSetTitle) {
      this.wordSetTitle = wordSetTitle;
    }

    public List<QuizQuestion> getQuestions() {
      return questions;
    }

    public void setQuestions(List<QuizQuestion> questions) {
      this.questions = questions;
    }

    public int getCurrentQuestionIndex() {
      return currentQuestionIndex;
    }

    public void setCurrentQuestionIndex(int currentQuestionIndex) {
      this.currentQuestionIndex = currentQuestionIndex;
    }

    public int getScore() {
      return score;
    }

    public void setScore(int score) {
      this.score = score;
    }

    public void incrementScore() {
      this.score++;
    }

    public List<Word> getIncorrectWords() {
      return incorrectWords;
    }

    public void setIncorrectWords(List<Word> incorrectWords) {
      this.incorrectWords = incorrectWords;
    }

    public void addIncorrectWord(Word word) {
      this.incorrectWords.add(word);
    }

    public LocalDateTime getStartedAt() {
      return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
      this.startedAt = startedAt;
    }
  }

  private static class QuizQuestion {
    private Word word;
    private String question;
    private String correctAnswer;
    private List<String> options;
    private String questionType;

    public Word getWord() {
      return word;
    }

    public void setWord(Word word) {
      this.word = word;
    }

    public String getQuestion() {
      return question;
    }

    public void setQuestion(String question) {
      this.question = question;
    }

    public String getCorrectAnswer() {
      return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
      this.correctAnswer = correctAnswer;
    }

    public List<String> getOptions() {
      return options;
    }

    public void setOptions(List<String> options) {
      this.options = options;
    }

    public String getQuestionType() {
      return questionType;
    }

    public void setQuestionType(String questionType) {
      this.questionType = questionType;
    }
  }
}
