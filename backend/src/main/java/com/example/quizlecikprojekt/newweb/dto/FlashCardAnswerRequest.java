package com.example.quizlecikprojekt.newweb.dto;

import jakarta.validation.constraints.NotNull;

public class FlashCardAnswerRequest {
    @NotNull(message = "Word ID is required")
    private Long wordId;

    @NotNull(message = "Answer is required")
    private Boolean correct;

    public FlashCardAnswerRequest() {}

    public Long getWordId() { return wordId; }
    public void setWordId(Long wordId) { this.wordId = wordId; }

    public Boolean isCorrect() { return correct; }
    public void setCorrect(Boolean correct) { this.correct = correct; }
}