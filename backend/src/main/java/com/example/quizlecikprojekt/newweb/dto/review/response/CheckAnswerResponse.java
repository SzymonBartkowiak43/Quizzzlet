package com.example.quizlecikprojekt.newweb.dto.review.response;

public record CheckAnswerResponse(
    boolean userCorrect, boolean wordCorrect, int correctWordCount, int totalWords) {}
