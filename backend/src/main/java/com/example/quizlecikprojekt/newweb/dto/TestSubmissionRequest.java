package com.example.quizlecikprojekt.newweb.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

public class TestSubmissionRequest {
    @NotNull(message = "Answers are required")
    private Map<Long, String> answers; // wordId -> userAnswer

    public TestSubmissionRequest() {}

    public Map<Long, String> getAnswers() { return answers; }
    public void setAnswers(Map<Long, String> answers) { this.answers = answers; }
}