package com.example.quizlecikprojekt.domain.user.dto;

public record UserRegistrationDto(
        String email,
        String password,
        String username
) {
}
