package com.example.quizlecikprojekt.controllers.dto.message;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;

public class ShareWordSetToGroupRequest {

    @NotNull(message = "ID grupy nie może być puste")
    @Positive(message = "ID grupy musi być dodatnie")
    private Long groupId;

    @NotNull(message = "ID zestawu słówek nie może być puste")
    @Positive(message = "ID zestawu słówek musi być dodatnie")
    private Long wordSetId;

    @NotBlank(message = "Wiadomość nie może być pusta")
    @Size(max = 500, message = "Wiadomość nie może przekraczać 500 znaków")
    private String message;

    // Constructors
    public ShareWordSetToGroupRequest() {}

    public ShareWordSetToGroupRequest(Long groupId, Long wordSetId, String message) {
        this.groupId = groupId;
        this.wordSetId = wordSetId;
        this.message = message;
    }

    // Getters and Setters
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public Long getWordSetId() { return wordSetId; }
    public void setWordSetId(Long wordSetId) { this.wordSetId = wordSetId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
