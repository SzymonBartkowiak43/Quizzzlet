package com.example.quizlecikprojekt.domain.progress.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RecordStudySessionRequest(
    @NotNull(message = "Word set ID is required") Long wordSetId,
    @NotNull(message = "Total words studied is required")
        @Min(value = 1, message = "Must study at least 1 word")
        Integer totalWordsStudied,
    @NotNull(message = "Correct answers count is required")
        @Min(value = 0, message = "Correct answers cannot be negative")
        Integer correctAnswers,
    @NotNull(message = "Incorrect answers count is required")
        @Min(value = 0, message = "Incorrect answers cannot be negative")
        Integer incorrectAnswers,
    @Min(value = 0, message = "Flashcards completed cannot be negative")
        Integer flashcardsCompleted,
    @Min(value = 0, message = "Quizzes completed cannot be negative") Integer quizzesCompleted,
    @Min(value = 1, message = "Study time must be at least 1 minute") Integer studyTimeMinutes,
    String sessionType
    ) {}
