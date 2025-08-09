package com.example.quizlecikprojekt.domain.progress;

import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.video.Video;
import com.example.quizlecikprojekt.domain.wordset.WordSet;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "resource_evaluations",
    uniqueConstraints = {
      @UniqueConstraint(columnNames = {"user_id", "word_set_id"}),
      @UniqueConstraint(columnNames = {"user_id", "video_id"})
    })
public class ResourceEvaluation{

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
  private Integer rating; // 1-5 stars

  @Column(name = "difficulty_level")
  @Enumerated(EnumType.STRING)
  private DifficultyLevel difficultyLevel;

  @Column(name = "usefulness_rating", nullable = false)
  private Integer usefulnessRating; // 1-5 stars

  @Column(name = "comment", length = 1000)
  private String comment;

  @Column(name = "would_recommend", nullable = false)
  private Boolean wouldRecommend = false;

  @Column(name = "completion_time_minutes")
  private Integer completionTimeMinutes;

  @Column(name = "tags")
  private String tags; // Comma-separated tags

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

  public Video getVideo() {
    return video;
  }

  public void setVideo(Video video) {
    this.video = video;
  }

  public Integer getRating() {
    return rating;
  }

  public void setRating(Integer rating) {
    this.rating = rating;
  }

  public DifficultyLevel getDifficultyLevel() {
    return difficultyLevel;
  }

  public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
    this.difficultyLevel = difficultyLevel;
  }

  public Integer getUsefulnessRating() {
    return usefulnessRating;
  }

  public void setUsefulnessRating(Integer usefulnessRating) {
    this.usefulnessRating = usefulnessRating;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public Boolean getWouldRecommend() {
    return wouldRecommend;
  }

  public void setWouldRecommend(Boolean wouldRecommend) {
    this.wouldRecommend = wouldRecommend;
  }

  public Integer getCompletionTimeMinutes() {
    return completionTimeMinutes;
  }

  public void setCompletionTimeMinutes(Integer completionTimeMinutes) {
    this.completionTimeMinutes = completionTimeMinutes;
  }

  public String getTags() {
    return tags;
  }

  public void setTags(String tags) {
    this.tags = tags;
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
