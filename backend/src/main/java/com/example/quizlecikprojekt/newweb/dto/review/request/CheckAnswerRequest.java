package com.example.quizlecikprojekt.newweb.dto.review.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CheckAnswerRequest(
    @NotBlank(message = "Word is required") String word,
    @NotBlank(message = "Translation is required") String translation,
    @NotNull(message = "User answer status is required") Boolean userCorrect) {}
