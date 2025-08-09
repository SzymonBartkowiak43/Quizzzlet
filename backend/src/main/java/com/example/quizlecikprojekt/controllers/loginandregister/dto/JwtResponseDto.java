package com.example.quizlecikprojekt.controllers.loginandregister.dto;

import lombok.Builder;

@Builder
public record JwtResponseDto(String email, String token) {}
