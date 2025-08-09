package com.example.quizlecikprojekt.controllers.dto.video;

import java.time.LocalDateTime;

public record CommentResponse(
    Long id, String content, String authorName, LocalDateTime createdAt) {}
