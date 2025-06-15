package com.example.quizlecikprojekt.newweb.dto.video;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String content,
        String authorUsername,
        LocalDateTime createdAt
) {
}
