package com.example.quizlecikprojekt.newweb.dto;

import java.util.List;

public record TestStartResponse (
    Long wordSetId,
    List<WordResponse> words,
     int totalQuestions
)
    {}