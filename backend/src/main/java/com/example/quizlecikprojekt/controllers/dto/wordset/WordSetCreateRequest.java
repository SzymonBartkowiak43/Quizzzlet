package com.example.quizlecikprojekt.controllers.dto.wordset;

import jakarta.validation.constraints.Size;

public record WordSetCreateRequest(
    @Size(max = 100, message = "Name cannot be longer than 100 characters") String name,
    @Size(max = 500, message = "Description cannot be longer than 500 characters")
        String description) {}
