package com.example.quizlecikprojekt.user.Dto;

import lombok.Getter;

import java.util.Set;

@Getter
public class UserDto {
    private final String email;
    private final String password;
    private final Set<String> roles;

    public UserDto(String email, String password, Set<String> roles) {
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

}
