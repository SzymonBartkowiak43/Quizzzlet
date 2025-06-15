package com.example.quizlecikprojekt.newweb.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record TestSubmissionRequest (
    @NotNull(message = "Answers are required")
    Map<Long, String> answers){}