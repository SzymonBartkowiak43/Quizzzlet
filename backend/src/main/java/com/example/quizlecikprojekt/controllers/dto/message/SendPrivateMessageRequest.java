package com.example.quizlecikprojekt.controllers.dto.message;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;

public class SendPrivateMessageRequest {

    @NotNull(message = "ID odbiorcy nie może być puste")
    @Positive(message = "ID odbiorcy musi być dodatnie")
    private Long recipientId;

    @NotBlank(message = "Treść wiadomości nie może być pusta")
    @Size(max = 1000, message = "Wiadomość nie może przekraczać 1000 znaków")
    private String content;

    // Constructors
    public SendPrivateMessageRequest() {}

    public SendPrivateMessageRequest(Long recipientId, String content) {
        this.recipientId = recipientId;
        this.content = content;
    }

    // Getters and Setters
    public Long getRecipientId() { return recipientId; }
    public void setRecipientId(Long recipientId) { this.recipientId = recipientId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
