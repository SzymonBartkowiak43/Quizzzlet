package com.example.quizlecikprojekt.controllers.dto;

import java.util.List;

public record UserDto(Long id, String name, String email, List<String> roles) {}