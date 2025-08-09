package com.example.quizlecikprojekt.newweb.dto.learn;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AnswerFlashcardRequest(
        @NotNull(message = "Session ID is required")
        @NotBlank(message = "Session ID cannot be blank")
        String sessionId,

        @NotNull(message = "Answer is required")
        boolean isCorrect
) {}