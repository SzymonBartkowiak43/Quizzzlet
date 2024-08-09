package com.example.quizlecikprojekt.domain.user.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationDto {
    private String email;
    private String password;
    private String username;
}
