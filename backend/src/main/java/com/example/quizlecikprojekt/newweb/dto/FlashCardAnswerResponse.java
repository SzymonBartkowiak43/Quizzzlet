package com.example.quizlecikprojekt.newweb.dto;

import com.example.quizlecikprojekt.newweb.dto.word.WordResponse;

import java.util.List;

public record FlashCardAnswerResponse(
        int score,
        int currentIndex,
        int totalWords,
        boolean completed,
        WordResponse nextWord,
        List<WordResponse> uncorrectedWords) {
}