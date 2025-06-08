package com.example.quizlecikprojekt.newweb.dto.profil;

import jakarta.validation.constraints.NotBlank;

public class PasswordVerificationRequest {
    @NotBlank(message = "Password is required")
    private String password;

    public PasswordVerificationRequest() {}

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}