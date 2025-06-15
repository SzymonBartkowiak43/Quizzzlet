package com.example.quizlecikprojekt.newweb.dto.review.response;

public record NextWordResponse(
        WordToRepeatResponse word,
        int correctWordCount,
        int totalWords
) {
}