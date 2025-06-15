package com.example.quizlecikprojekt.newweb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WordSetUpdateRequest (
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot be longer than 100 characters")
    String name,

    @Size(max = 500, message = "Description cannot be longer than 500 characters")
     String description
){}