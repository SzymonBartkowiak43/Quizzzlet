package com.example.quizlecikprojekt.domain.learn;

import com.example.quizlecikprojekt.controllers.dto.learn.*;
import com.example.quizlecikprojekt.domain.word.Word;
import com.example.quizlecikprojekt.domain.word.WordFacade;
import com.example.quizlecikprojekt.domain.wordset.WordSet;
import com.example.quizlecikprojekt.domain.wordset.WordSetFacade;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LearnFacade {

  private final WordSetFacade wordSetFacade;
  private final WordFacade wordFacade;

  private final Map<String, FlashcardSession> flashcardSessions = new ConcurrentHashMap<>();
  private final Map<String, QuizSession> quizSessions = new ConcurrentHashMap<>();

  // Flashcard methods
  public FlashcardSessionResponse startFlashcardSession(
      String userEmail, StartFlashcardRequest request) {
    WordSet wordSet = wordSetFacade.getWordSetById(request.wordSetId());
    List<Word> words = wordSet.getWords();

    if (words.isEmpty()) {
      throw new IllegalArgumentException("Word set has no words to learn");
    }

    Collections.shuffle(words);

    String sessionId = generateSessionId();
    FlashcardSession session = createFlashcardSession(sessionId, wordSet, words);

    flashcardSessions.put(sessionId, session);

    return buildFlashcardResponse(session);
  }

  public FlashcardSessionResponse answerFlashcard(AnswerFlashcardRequest request) {
    FlashcardSession session = getFlashcardSession(request.sessionId());

    Word currentWord = session.getWords().get(session.getCurrentIndex());

    if (request.isCorrect()) {
      session.incrementScore();
      wordFacade.updateWordPoints(currentWord.getId(), currentWord.getPoints() + 1);
    } else {
      session.addIncorrectWord(currentWord);
    }

    session.setCurrentIndex(session.getCurrentIndex() + 1);

    return buildFlashcardResponse(session);
  }

  public FlashcardSessionResponse getFlashcardSessionResponse(String sessionId) {
    FlashcardSession session = getFlashcardSession(sessionId);
    return buildFlashcardResponse(session);
  }

  public SessionSummaryResponse endFlashcardSession(String sessionId) {
    FlashcardSession session = flashcardSessions.remove(sessionId);
    if (session == null) {
      throw new IllegalArgumentException("Flashcard session not found");
    }

    return buildSessionSummary(session, "flashcard");
  }

  public QuizSessionResponse startQuizSession(String userEmail, StartQuizRequest request) {

    WordSet wordSet = wordSetFacade.getWordSetById(request.wordSetId());
    List<Word> allWords = wordSet.getWords();

    if (allWords.isEmpty()) {
      throw new IllegalArgumentException("Word set has no words for quiz");
    }

    List<Word> quizWords = selectQuizWords(allWords, request.numberOfQuestions());

    String sessionId = generateSessionId();
    QuizSession session = createQuizSession(sessionId, wordSet, quizWords, allWords);

    quizSessions.put(sessionId, session);

    return buildQuizResponse(session);
  }

  public QuizSessionResponse answerQuiz(AnswerQuizRequest request) {
    QuizSession session = getQuizSession(request.sessionId());

    QuizQuestion currentQuestion = session.getQuestions().get(session.getCurrentQuestionIndex());
    boolean isCorrect =
        currentQuestion.getCorrectAnswer().equalsIgnoreCase(request.answer().trim());

    if (isCorrect) {
      session.incrementScore();
      wordFacade.updateWordPoints(
          currentQuestion.getWord().getId(), currentQuestion.getWord().getPoints() + 1);
    } else {
      session.addIncorrectWord(currentQuestion.getWord());
    }

    session.setCurrentQuestionIndex(session.getCurrentQuestionIndex() + 1);

    return buildQuizResponse(session);
  }

  public QuizSessionResponse getQuizSessionResponse(String sessionId) {
    QuizSession session = getQuizSession(sessionId);
    return buildQuizResponse(session);
  }

  public SessionSummaryResponse endQuizSession(String sessionId) {
    QuizSession session = quizSessions.remove(sessionId);
    if (session == null) {
      throw new IllegalArgumentException("Quiz session not found");
    }

    return buildSessionSummary(session, "quiz");
  }

  // Private helper methods
  private FlashcardSession getFlashcardSession(String sessionId) {
    FlashcardSession session = flashcardSessions.get(sessionId);
    if (session == null) {
      throw new IllegalArgumentException("Flashcard session not found");
    }
    return session;
  }

  private QuizSession getQuizSession(String sessionId) {
    QuizSession session = quizSessions.get(sessionId);
    if (session == null) {
      throw new IllegalArgumentException("Quiz session not found");
    }
    return session;
  }

  private String generateSessionId() {
    return UUID.randomUUID().toString();
  }

  private FlashcardSession createFlashcardSession(
      String sessionId, WordSet wordSet, List<Word> words) {
    FlashcardSession session = new FlashcardSession();
    session.setSessionId(sessionId);
    session.setWordSetId(wordSet.getId());
    session.setWordSetTitle(wordSet.getTitle());
    session.setWords(words);
    session.setCurrentIndex(0);
    session.setScore(0);
    session.setIncorrectWords(new ArrayList<>());
    session.setStartedAt(LocalDateTime.now());
    return session;
  }

  private QuizSession createQuizSession(
      String sessionId, WordSet wordSet, List<Word> quizWords, List<Word> allWords) {
    QuizSession session = new QuizSession();
    session.setSessionId(sessionId);
    session.setWordSetId(wordSet.getId());
    session.setWordSetTitle(wordSet.getTitle());
    session.setQuestions(generateQuizQuestions(quizWords, allWords));
    session.setCurrentQuestionIndex(0);
    session.setScore(0);
    session.setIncorrectWords(new ArrayList<>());
    session.setStartedAt(LocalDateTime.now());
    return session;
  }

  private List<Word> selectQuizWords(List<Word> allWords, int numberOfQuestions) {
    if (numberOfQuestions > 0 && numberOfQuestions < allWords.size()) {
      Collections.shuffle(allWords);
      return allWords.subList(0, numberOfQuestions);
    }
    return allWords;
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
          !fs.getWords().isEmpty() ? (double) fs.getScore() / fs.getWords().size() * 100 : 0;

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
          !qs.getQuestions().isEmpty()
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
}
