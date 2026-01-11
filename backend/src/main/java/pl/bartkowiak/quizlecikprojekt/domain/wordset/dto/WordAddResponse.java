package pl.bartkowiak.quizlecikprojekt.domain.wordset.dto;

import pl.bartkowiak.quizlecikprojekt.controllers.dto.word.WordResponse;
import java.util.List;

public record WordAddResponse(List<WordResponse> addedWords, int totalAdded, String message) {}
