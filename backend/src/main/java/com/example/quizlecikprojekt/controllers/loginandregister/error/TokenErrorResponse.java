package com.example.quizlecikprojekt.controllers.loginandregister.error;

import org.springframework.http.HttpStatus;

public record TokenErrorResponse(String message, HttpStatus status) {}
