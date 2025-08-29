package com.example.quizlecikprojekt.domain.group;

import com.example.quizlecikprojekt.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class GroupMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Group group;

    @ManyToOne
    private User sender;

    private String content;

    private LocalDateTime createdAt = LocalDateTime.now();

    public GroupMessage() {}

    public GroupMessage(Group group, User sender, String content) {
        this.group = group;
        this.sender = sender;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }
}