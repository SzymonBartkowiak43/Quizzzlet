package com.example.quizlecikprojekt.domain.word.dto;

import com.example.quizlecikprojekt.newweb.dto.word.WordResponse;
import java.util.List;

public record WordAddResponse(List<WordResponse> addedWords, int totalAdded, String message) {}
