package com.example.quizlecikprojekt.controllers.dto.message;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;

public class ShareWordSetRequest {

    @NotNull(message = "ID odbiorcy nie może być puste")
    @Positive(message = "ID odbiorcy musi być dodatnie")
    private Long recipientId;

    @NotNull(message = "ID zestawu słówek nie może być puste")
    @Positive(message = "ID zestawu słówek musi być dodatnie")
    private Long wordSetId;

    @NotBlank(message = "Wiadomość nie może być pusta")
    @Size(max = 500, message = "Wiadomość nie może przekraczać 500 znaków")
    private String message;

    // Constructors
    public ShareWordSetRequest() {}

    public ShareWordSetRequest(Long recipientId, Long wordSetId, String message) {
        this.recipientId = recipientId;
        this.wordSetId = wordSetId;
        this.message = message;
    }

    // Getters and Setters
    public Long getRecipientId() { return recipientId; }
    public void setRecipientId(Long recipientId) { this.recipientId = recipientId; }

    public Long getWordSetId() { return wordSetId; }
    public void setWordSetId(Long wordSetId) { this.wordSetId = wordSetId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}