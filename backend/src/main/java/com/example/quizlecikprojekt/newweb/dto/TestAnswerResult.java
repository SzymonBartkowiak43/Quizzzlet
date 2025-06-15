package com.example.quizlecikprojekt.newweb.dto;

public record TestAnswerResult (
    Long wordId,
     String word,
     String correctAnswer,
     String userAnswer,
     boolean correct
)
    {}