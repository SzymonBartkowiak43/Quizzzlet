package com.example.quizlecikprojekt.entity;

import com.example.quizlecikprojekt.entity.User;
import com.example.quizlecikprojekt.entity.Video;
import com.example.quizlecikprojekt.entity.WordSet;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(
    name = "resource_evaluations",
    uniqueConstraints = {
      @UniqueConstraint(columnNames = {"user_id", "word_set_id"}),
      @UniqueConstraint(columnNames = {"user_id", "video_id"})
    })
@Getter
@Setter
public class ResourceEvaluation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "word_set_id")
  private WordSet wordSet;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "video_id")
  private Video video;

  @Column(name = "rating", nullable = false)
  private Integer rating;

  @Column(name = "difficulty_level")
  @Enumerated(EnumType.STRING)
  private DifficultyLevel difficultyLevel;

  @Column(name = "usefulness_rating", nullable = false)
  private Integer usefulnessRating;

  @Column(name = "comment", length = 1000)
  private String comment;

  @Column(name = "would_recommend", nullable = false)
  private Boolean wouldRecommend = false;

  @Column(name = "completion_time_minutes")
  private Integer completionTimeMinutes;

  @Column(name = "tags")
  private String tags;

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
  }

  @PreUpdate
  private void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  public enum DifficultyLevel {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED,
    EXPERT
  }
}
