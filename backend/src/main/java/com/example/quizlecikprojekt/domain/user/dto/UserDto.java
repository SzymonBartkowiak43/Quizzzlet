package com.example.quizlecikprojekt.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserDto {
    private String email;
    private  String userName;
    private String password;
    private Set<String> roles;

}
