package com.example.quizlecikprojekt.newweb.dto.profil.request;

import jakarta.validation.constraints.NotBlank;

public record PasswordVerificationRequest(
        @NotBlank(message = "Password is required")
        String password) {
}