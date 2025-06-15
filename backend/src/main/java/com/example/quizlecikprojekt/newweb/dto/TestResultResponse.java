package com.example.quizlecikprojekt.newweb.dto;


import java.util.List;

public record TestResultResponse (
    int score,
    int totalQuestions,
    double percentage,
    List<TestAnswerResult> results,
    List<WordResponse> correctWords,
    List<WordResponse> incorrectWords
)
{}