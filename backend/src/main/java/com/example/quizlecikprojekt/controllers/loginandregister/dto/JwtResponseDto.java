package com.example.quizlecikprojekt.controllers.loginandregister.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class JwtResponseDto {
    private String token;
    private UserSummary user;

    @Data
    @AllArgsConstructor
    public static class UserSummary {
        private Long id;
        private String email;
        private String userName;
        private List<String> roles;
    }
}
