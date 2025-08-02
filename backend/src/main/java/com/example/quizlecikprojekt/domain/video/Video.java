package com.example.quizlecikprojekt.domain.video;

import com.example.quizlecikprojekt.domain.comment.Comment;
import com.example.quizlecikprojekt.domain.rating.Rating;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
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

  @Column(name = "date_and_time", nullable = false)
  private LocalDateTime createdAt;

  @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Comment> comments = new HashSet<>();

  @OneToMany(mappedBy = "video")
  private Set<Rating> ratings = new HashSet<>();
}
