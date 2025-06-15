package com.example.quizlecikprojekt.newweb.dto.test;

import com.example.quizlecikprojekt.newweb.dto.word.WordResponse;

import java.util.List;

public record TestStartResponse(
        Long wordSetId,
        List<WordResponse> words,
        int totalQuestions
) {
}