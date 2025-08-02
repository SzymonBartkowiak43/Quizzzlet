package com.example.quizlecikprojekt.newweb.dto.video;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddVideoRequest(
    @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title cannot be longer than 200 characters")
        String title,
    @NotBlank(message = "URL is required")
        @Pattern(
            regexp = "^(https?://)?(www\\.)?(youtube\\.com|youtu\\.be)/.+",
            message = "Must be a valid YouTube URL")
        String url) {}
