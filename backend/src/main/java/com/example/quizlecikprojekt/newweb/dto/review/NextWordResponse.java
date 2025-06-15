package com.example.quizlecikprojekt.newweb.dto.review;

public record NextWordResponse (
    WordToRepeatResponse word,
    int correctWordCount,
    int totalWords
){}