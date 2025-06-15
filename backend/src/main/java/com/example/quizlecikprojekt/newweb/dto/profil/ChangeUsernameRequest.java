package com.example.quizlecikprojekt.newweb.dto.profil;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeUsernameRequest (
    @NotBlank(message = "Current password is required")
    String currentPassword,

    @NotBlank(message = "New username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters") String newUsername )
        {
}