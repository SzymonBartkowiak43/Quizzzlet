package com.example.quizlecikprojekt.newweb.dto;

import jakarta.validation.constraints.Size;

public class WordSetCreateRequest {
    @Size(max = 100, message = "Name cannot be longer than 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot be longer than 500 characters")
    private String description;

    // constructors, getters, setters
    public WordSetCreateRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}