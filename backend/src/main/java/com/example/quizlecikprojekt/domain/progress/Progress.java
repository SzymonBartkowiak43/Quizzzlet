package com.example.quizlecikprojekt.domain.progress;

import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.wordset.WordSet;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "progress")
@Getter
public class Progress {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "word_set_id")
  private WordSet wordSet;

  @Column(name = "study_date", nullable = false)
  private LocalDate studyDate;

  @Column(name = "total_words_studied", nullable = false)
  private Integer totalWordsStudied = 0;

  @Column(name = "correct_answers", nullable = false)
  private Integer correctAnswers = 0;

  @Column(name = "incorrect_answers", nullable = false)
  private Integer incorrectAnswers = 0;

  @Column(name = "flashcards_completed", nullable = false)
  private Integer flashcardsCompleted = 0;

  @Column(name = "quizzes_completed", nullable = false)
  private Integer quizzesCompleted = 0;

  @Column(name = "study_time_minutes", nullable = false)
  private Integer studyTimeMinutes = 0;

  @Column(name = "streak_count", nullable = false)
  private Integer streakCount = 0;

  @Column(name = "accuracy_percentage")
  private Double accuracyPercentage = 0.0;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @PrePersist
  private void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = createdAt;
    updateAccuracy();
  }

  @PreUpdate
  private void onUpdate() {
    updatedAt = LocalDateTime.now();
    updateAccuracy();
  }

  private void updateAccuracy() {
    int total = correctAnswers + incorrectAnswers;
    if (total > 0) {
      accuracyPercentage = (double) correctAnswers / total * 100.0;
    } else {
      accuracyPercentage = 0.0;
    }
  }

  // Getters and setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public WordSet getWordSet() {
    return wordSet;
  }

  public void setWordSet(WordSet wordSet) {
    this.wordSet = wordSet;
  }

  public LocalDate getStudyDate() {
    return studyDate;
  }

  public void setStudyDate(LocalDate studyDate) {
    this.studyDate = studyDate;
  }

  public Integer getTotalWordsStudied() {
    return totalWordsStudied;
  }

  public void setTotalWordsStudied(Integer totalWordsStudied) {
    this.totalWordsStudied = totalWordsStudied;
  }

  public Integer getCorrectAnswers() {
    return correctAnswers;
  }

  public void setCorrectAnswers(Integer correctAnswers) {
    this.correctAnswers = correctAnswers;
  }

  public Integer getIncorrectAnswers() {
    return incorrectAnswers;
  }

  public void setIncorrectAnswers(Integer incorrectAnswers) {
    this.incorrectAnswers = incorrectAnswers;
  }

  public Integer getFlashcardsCompleted() {
    return flashcardsCompleted;
  }

  public void setFlashcardsCompleted(Integer flashcardsCompleted) {
    this.flashcardsCompleted = flashcardsCompleted;
  }

  public Integer getQuizzesCompleted() {
    return quizzesCompleted;
  }

  public void setQuizzesCompleted(Integer quizzesCompleted) {
    this.quizzesCompleted = quizzesCompleted;
  }

  public Integer getStudyTimeMinutes() {
    return studyTimeMinutes;
  }

  public void setStudyTimeMinutes(Integer studyTimeMinutes) {
    this.studyTimeMinutes = studyTimeMinutes;
  }

  public Integer getStreakCount() {
    return streakCount;
  }

  public void setStreakCount(Integer streakCount) {
    this.streakCount = streakCount;
  }

  public Double getAccuracyPercentage() {
    return accuracyPercentage;
  }

  public void setAccuracyPercentage(Double accuracyPercentage) {
    this.accuracyPercentage = accuracyPercentage;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
