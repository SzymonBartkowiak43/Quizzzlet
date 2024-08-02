package com.example.quizlecikprojekt.user;

import com.example.quizlecikprojekt.user.Dto.UserDto;

import java.util.Set;
import java.util.stream.Collectors;

class UserCredentialsDtoMapper {
    static UserDto map(User user) {
        String email = user.getEmail();
        String password = user.getPassword();
        String username = user.getUserName();
        Set<String> roles = user.getRoles()
                .stream()
                .map(UserRole::getName)
                .collect(Collectors.toSet());
        return new UserDto(email,username, password, roles);
    }
}