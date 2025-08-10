package com.example.quizlecikprojekt.domain.word.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record WordAddRequest(
    @Valid
        @NotNull(message = "Words list cannot be null")
        @NotEmpty(message = "Words list cannot be empty")
        List<WordItem> words) {}
