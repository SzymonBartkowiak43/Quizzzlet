package com.example.quizlecikprojekt.newweb.dto.video;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddCommentRequest(
        @NotBlank(message = "Comment content is required")
        @Size(max = 1000, message = "Comment cannot be longer than 1000 characters")
        String content
) {
}