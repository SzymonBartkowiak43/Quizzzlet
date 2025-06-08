package com.example.quizlecikprojekt.newweb.dto.video;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AddVideoRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title cannot be longer than 200 characters")
    private String title;

    @NotBlank(message = "URL is required")
    @Pattern(regexp = "^(https?://)?(www\\.)?(youtube\\.com|youtu\\.be)/.+",
            message = "Must be a valid YouTube URL")
    private String url;

    public AddVideoRequest() {}

    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}