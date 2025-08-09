package com.example.quizlecikprojekt.controllers.dto.wordset;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WordUpdateRequest(
    @NotBlank(message = "Word is required")
        @Size(min = 1, max = 100, message = "Word must be between 1 and 100 characters")
        String word,
    @NotBlank(message = "Translation is required")
        @Size(min = 1, max = 100, message = "Translation must be between 1 and 100 characters")
        String translation) {}
