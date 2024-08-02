package com.example.quizlecikprojekt.user.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserDto {
    private  String email;
    private String userName;
    private  String password;
    private  Set<String> roles;


    public UserDto(String email,String userName, String password, Set<String> roles) {
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.userName = userName;
    }

    public UserDto() {
    }

}
