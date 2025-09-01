package com.example.quizlecikprojekt.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "progress")
@Getter
@Setter
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
}
