package com.example.quizlecikprojekt.newweb.dto.video;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddVideoRequest(
        @NotBlank(message = "URL is required")
        @Size(max = 500, message = "URL cannot exceed 500 characters")
        String url,

        @NotBlank(message = "Title is required")
        @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
        String title
) {}