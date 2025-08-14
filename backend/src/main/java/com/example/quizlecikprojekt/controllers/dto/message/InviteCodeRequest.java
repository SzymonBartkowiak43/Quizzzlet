package com.example.quizlecikprojekt.controllers.dto.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class InviteCodeRequest {

    @NotBlank(message = "Kod zaproszenia nie może być pusty")
    @Pattern(regexp = "^[A-Z0-9]{8}$", message = "Nieprawidłowy format kodu zaproszenia")
    private String inviteCode;

    // Constructors
    public InviteCodeRequest() {}

    public InviteCodeRequest(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    // Getters and Setters
    public String getInviteCode() { return inviteCode; }
    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }
}
