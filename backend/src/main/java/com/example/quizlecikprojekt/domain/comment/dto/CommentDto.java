package com.example.quizlecikprojekt.domain.comment.dto;

import com.example.quizlecikprojekt.domain.user.User;

import java.time.LocalDateTime;

public record CommentDto(
        Long id,
        String content,
        User user,
        LocalDateTime dateAndTime
) {
}
