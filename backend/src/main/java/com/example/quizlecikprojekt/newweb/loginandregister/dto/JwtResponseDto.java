package com.example.quizlecikprojekt.newweb.loginandregister.dto;

import lombok.Builder;

@Builder
public record JwtResponseDto(
        String email,
        String token
) {
}
