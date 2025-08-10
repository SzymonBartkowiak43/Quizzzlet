package com.example.quizlecikprojekt.domain.learn;

import com.example.quizlecikprojekt.domain.word.Word;
import java.time.LocalDateTime;
import java.util.List;

public class QuizSession {
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
