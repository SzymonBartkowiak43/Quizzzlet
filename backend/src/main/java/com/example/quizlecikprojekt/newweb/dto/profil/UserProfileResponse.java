package com.example.quizlecikprojekt.newweb.dto.profil;

public class UserProfileResponse {
    private String email;
    private String userName;

    public UserProfileResponse() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}