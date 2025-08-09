package com.example.quizlecikprojekt.controllers.dto.learn;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AnswerQuizRequest(
    @NotNull(message = "Session ID is required") @NotBlank(message = "Session ID cannot be blank")
        String sessionId,
    @NotNull(message = "Answer is required") @NotBlank(message = "Answer cannot be blank")
        String answer) {}
