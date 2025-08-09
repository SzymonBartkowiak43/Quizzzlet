package com.example.quizlecikprojekt.controllers.dto.learn;

import jakarta.validation.constraints.NotNull;

public record StartFlashcardRequest(@NotNull(message = "Word set ID is required") Long wordSetId) {}
