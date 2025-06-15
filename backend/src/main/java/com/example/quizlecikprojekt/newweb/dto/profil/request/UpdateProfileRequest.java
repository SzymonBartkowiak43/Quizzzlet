package com.example.quizlecikprojekt.newweb.dto.profil.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String userName,

        @NotBlank(message = "Current password is required")
        String currentPassword,

        @Size(min = 6, message = "New password must be at least 6 characters long")
        String newPassword) {
}
