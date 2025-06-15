package com.example.quizlecikprojekt.newweb.dto;

import com.example.quizlecikprojekt.domain.word.Word;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record WordSetResponse (
    Long id,
    String title,
    String description,
    String language,
   String translationLanguage,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
     int wordCount,
     List<Word> words
){}
