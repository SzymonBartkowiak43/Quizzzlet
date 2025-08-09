package com.example.quizlecikprojekt.newweb.dto.word;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record WordDeleteRequest(
    @NotNull(message = "Word IDs list is required")
        @NotEmpty(message = "At least one word ID must be provided")
        List<Long> wordIds) {}
