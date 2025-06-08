package com.example.quizlecikprojekt.newweb.dto;

public class HomeResponse {
    private boolean isLoggedIn;
    private String username;

    public HomeResponse() {}

    public HomeResponse(boolean isLoggedIn, String username) {
        this.isLoggedIn = isLoggedIn;
        this.username = username;
    }

    // Getters and setters
    public boolean isLoggedIn() { return isLoggedIn; }
    public void setLoggedIn(boolean loggedIn) { isLoggedIn = loggedIn; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}