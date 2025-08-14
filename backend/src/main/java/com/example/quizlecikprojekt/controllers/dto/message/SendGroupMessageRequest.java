package com.example.quizlecikprojekt.controllers.dto.message;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;

public class SendGroupMessageRequest {

    @NotNull(message = "ID grupy nie może być puste")
    @Positive(message = "ID grupy musi być dodatnie")
    private Long groupId;

    @NotBlank(message = "Treść wiadomości nie może być pusta")
    @Size(max = 1000, message = "Wiadomość nie może przekraczać 1000 znaków")
    private String content;

    // Constructors
    public SendGroupMessageRequest() {}

    public SendGroupMessageRequest(Long groupId, String content) {
        this.groupId = groupId;
        this.content = content;
    }

    // Getters and Setters
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
