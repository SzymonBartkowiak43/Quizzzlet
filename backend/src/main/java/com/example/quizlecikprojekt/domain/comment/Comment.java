package com.example.quizlecikprojekt.domain.comment;

import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.video.Video;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500, nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "date_and_time", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

}
