package com.example.quizlecikprojekt.user.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserDto {
    private  String email;
    private  String password;
    private  Set<String> roles;

    public UserDto(String email, String password, Set<String> roles) {
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public UserDto() {
    }

}
