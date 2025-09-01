package com.example.quizlecikprojekt.domain.wordset.dto;

import jakarta.validation.constraints.NotBlank;

public record WordItem(
    @NotBlank(message = "Word cannot be blank") String word,
    @NotBlank(message = "Translation cannot be blank") String translation) {}
