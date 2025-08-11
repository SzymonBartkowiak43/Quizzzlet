package com.example.quizlecikprojekt.domain.video;

import com.example.quizlecikprojekt.domain.comment.Comment;
import com.example.quizlecikprojekt.domain.rating.Rating;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
@Table(name = "videos")
public class Video {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String url;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Comment> comments = new HashSet<>();

  @OneToMany(mappedBy = "video")
  private Set<Rating> ratings = new HashSet<>();

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
}
