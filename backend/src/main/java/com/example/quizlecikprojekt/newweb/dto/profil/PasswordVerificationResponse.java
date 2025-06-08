package com.example.quizlecikprojekt.newweb.dto.profil;

public class PasswordVerificationResponse {
    private boolean valid;

    public PasswordVerificationResponse() {}

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
}