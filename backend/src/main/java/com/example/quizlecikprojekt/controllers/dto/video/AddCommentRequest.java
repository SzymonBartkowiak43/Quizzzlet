package com.example.quizlecikprojekt.controllers.dto.video;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddCommentRequest(
    @NotBlank(message = "Content is required")
        @Size(min = 1, max = 1000, message = "Content must be between 1 and 1000 characters")
        String content) {}
