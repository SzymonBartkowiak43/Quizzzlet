package com.example.quizlecikprojekt.newweb.dto.tranzlation;

public class TranslationResponse {
    private String originalText;
    private String translatedText;
    private String sourceLanguage;
    private String targetLanguage;
    private boolean translationSuccessful;
    private String message;

    public TranslationResponse() {}

    // Getters and setters
    public String getOriginalText() { return originalText; }
    public void setOriginalText(String originalText) { this.originalText = originalText; }

    public String getTranslatedText() { return translatedText; }
    public void setTranslatedText(String translatedText) { this.translatedText = translatedText; }

    public String getSourceLanguage() { return sourceLanguage; }
    public void setSourceLanguage(String sourceLanguage) { this.sourceLanguage = sourceLanguage; }

    public String getTargetLanguage() { return targetLanguage; }
    public void setTargetLanguage(String targetLanguage) { this.targetLanguage = targetLanguage; }

    public boolean isTranslationSuccessful() { return translationSuccessful; }
    public void setTranslationSuccessful(boolean translationSuccessful) { this.translationSuccessful = translationSuccessful; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
