package com.example.quizlecikprojekt.newweb.dto;

import jakarta.validation.constraints.NotNull;

public record FlashCardAnswerRequest(
        @NotNull(message = "Word ID is required")
        Long wordId,

        @NotNull(message = "Answer is required")
        Boolean correct) {
}