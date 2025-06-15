package com.example.quizlecikprojekt.newweb.dto.review;

public record WordToRepeatResponse(
    String word,
    String translation,
    boolean correct
) {}