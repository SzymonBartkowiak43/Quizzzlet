package com.example.quizlecikprojekt.newweb.dto.video;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AddCommentRequest {
    @NotBlank(message = "Comment content is required")
    @Size(max = 1000, message = "Comment cannot be longer than 1000 characters")
    private String content;

    public AddCommentRequest() {}

    // Getters and setters
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}