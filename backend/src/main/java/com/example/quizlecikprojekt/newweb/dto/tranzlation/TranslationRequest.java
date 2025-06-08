package com.example.quizlecikprojekt.newweb.dto.tranzlation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TranslationRequest {
    @NotBlank(message = "Text to translate is required")
    @Size(max = 1000, message = "Text cannot be longer than 1000 characters")
    private String text;

    @NotBlank(message = "Source language is required")
    @Size(min = 2, max = 5, message = "Language code must be 2-5 characters")
    private String sourceLanguage;

    @NotBlank(message = "Target language is required")
    @Size(min = 2, max = 5, message = "Language code must be 2-5 characters")
    private String targetLanguage;

    public TranslationRequest() {}

    // Getters and setters
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getSourceLanguage() { return sourceLanguage; }
    public void setSourceLanguage(String sourceLanguage) { this.sourceLanguage = sourceLanguage; }

    public String getTargetLanguage() { return targetLanguage; }
    public void setTargetLanguage(String targetLanguage) { this.targetLanguage = targetLanguage; }
}