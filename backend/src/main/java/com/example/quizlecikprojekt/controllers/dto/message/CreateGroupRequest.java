package com.example.quizlecikprojekt.controllers.dto.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public class CreateGroupRequest {

    @NotBlank(message = "Nazwa grupy nie może być pusta")
    @Size(min = 3, max = 100, message = "Nazwa grupy musi mieć od 3 do 100 znaków")
    private String name;

    @Size(max = 500, message = "Opis nie może przekraczać 500 znaków")
    private String description;

    private Boolean isPrivate = false;

    @Min(value = 2, message = "Minimalna liczba członków to 2")
    @Max(value = 100, message = "Maksymalna liczba członków to 100")
    private Integer maxMembers = 50;

    // Constructors
    public CreateGroupRequest() {}

    public CreateGroupRequest(String name, String description, Boolean isPrivate, Integer maxMembers) {
        this.name = name;
        this.description = description;
        this.isPrivate = isPrivate;
        this.maxMembers = maxMembers;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getIsPrivate() { return isPrivate; }
    public void setIsPrivate(Boolean isPrivate) { this.isPrivate = isPrivate; }

    public Integer getMaxMembers() { return maxMembers; }
    public void setMaxMembers(Integer maxMembers) { this.maxMembers = maxMembers; }
}