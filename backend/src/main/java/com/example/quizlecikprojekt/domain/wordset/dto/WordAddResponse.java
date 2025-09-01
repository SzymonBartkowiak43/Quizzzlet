package com.example.quizlecikprojekt.domain.wordset.dto;

import com.example.quizlecikprojekt.controllers.dto.word.WordResponse;
import java.util.List;

public record WordAddResponse(List<WordResponse> addedWords, int totalAdded, String message) {}
