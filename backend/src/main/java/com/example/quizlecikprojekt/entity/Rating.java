package com.example.quizlecikprojekt.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "video_rating")
@Setter
@Getter
public class Rating {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne
  @JoinColumn(name = "video_id")
  private Video video;

  private Integer rating;

  @Column(name = "date_and_time", nullable = false)
  private LocalDateTime dateAndTime;
}
