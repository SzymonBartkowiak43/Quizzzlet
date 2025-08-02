package com.example.quizlecikprojekt.newweb.dto.tranzlation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TranslationRequest(
    @NotBlank(message = "Text to translate is required")
        @Size(max = 1000, message = "Text cannot be longer than 1000 characters")
        String text,
    @NotBlank(message = "Source language is required")
        @Size(min = 2, max = 5, message = "Language code must be 2-5 characters")
        String sourceLanguage,
    @NotBlank(message = "Target language is required")
        @Size(min = 2, max = 5, message = "Language code must be 2-5 characters")
        String targetLanguage) {}
