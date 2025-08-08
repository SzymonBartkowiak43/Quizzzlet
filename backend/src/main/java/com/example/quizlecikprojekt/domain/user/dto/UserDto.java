package com.example.quizlecikprojekt.domain.user.dto;

import java.util.Set;
import lombok.*;

@Builder
public record UserDto(
    Long userId, String email, String name, String password, Set<UserRoleDto> roles) {}
