package com.example.quizlecikprojekt.controllers.dto.wordset;

import com.example.quizlecikprojekt.controllers.dto.word.WordResponse;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record WordSetResponse(
    Long id,
    String title,
    String description,
    String language,
    String translationLanguage,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    List<WordResponse> words) {}
