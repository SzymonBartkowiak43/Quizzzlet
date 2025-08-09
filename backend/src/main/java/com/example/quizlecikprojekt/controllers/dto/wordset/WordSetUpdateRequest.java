package com.example.quizlecikprojekt.controllers.dto.wordset;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WordSetUpdateRequest(
    @NotBlank(message = "Title is required")
        @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
        String title,
    @Size(max = 500, message = "Description cannot exceed 500 characters") String description,
    @Size(min = 2, max = 10, message = "Language code must be between 2 and 10 characters")
        String language,
    @Size(
            min = 2,
            max = 10,
            message = "Translation language code must be between 2 and 10 characters")
        String translationLanguage) {}
