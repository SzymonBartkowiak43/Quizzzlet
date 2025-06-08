package com.example.quizlecikprojekt.newweb.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CheckAnswerRequest {
    @NotBlank(message = "Word is required")
    private String word;

    @NotBlank(message = "Translation is required")
    private String translation;

    @NotNull(message = "User answer status is required")
    private Boolean userCorrect;

    public CheckAnswerRequest() {}

    // Getters and setters
    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }

    public String getTranslation() { return translation; }
    public void setTranslation(String translation) { this.translation = translation; }

    public Boolean isUserCorrect() { return userCorrect; }
    public void setUserCorrect(Boolean userCorrect) { this.userCorrect = userCorrect; }
}