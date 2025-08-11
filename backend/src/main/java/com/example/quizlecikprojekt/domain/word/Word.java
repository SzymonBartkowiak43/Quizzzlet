package com.example.quizlecikprojekt.domain.word;

import com.example.quizlecikprojekt.domain.wordset.WordSet;
import jakarta.persistence.*;
import java.sql.Date;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@Entity
@Table(name = "words")
public class Word {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String word;
  private String translation;
  private Integer points;
  private boolean star;
  private Date lastPracticed;

  @ManyToOne
  @JoinColumn(name = "word_set_id", nullable = false)
  private WordSet wordSet;

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
