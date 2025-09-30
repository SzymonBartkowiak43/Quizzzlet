package com.example.quizlecikprojekt.controllers.dto.learn;

import java.util.List;

public record QuizQuestionResponse(
    Long wordId,
    String question,
    List<String> options,
    String questionType
    ) {}
