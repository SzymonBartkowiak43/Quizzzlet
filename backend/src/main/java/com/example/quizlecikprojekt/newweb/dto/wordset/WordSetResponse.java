package com.example.quizlecikprojekt.newweb.dto.wordset;

import com.example.quizlecikprojekt.domain.word.Word;
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
    int wordCount,
    List<Word> words) {}
