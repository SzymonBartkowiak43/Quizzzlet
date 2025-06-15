package com.example.quizlecikprojekt.newweb.dto.test;


import com.example.quizlecikprojekt.newweb.dto.word.WordResponse;

import java.util.List;

public record TestResultResponse(
        int score,
        int totalQuestions,
        double percentage,
        List<TestAnswerResult> results,
        List<WordResponse> correctWords,
        List<WordResponse> incorrectWords
) {
}