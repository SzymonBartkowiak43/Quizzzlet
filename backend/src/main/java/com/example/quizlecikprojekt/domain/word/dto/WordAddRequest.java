package com.example.quizlecikprojekt.domain.word.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record WordAddRequest(
    @NotNull(message = "Words list cannot be null")
        @NotEmpty(message = "Words list cannot be empty")
        @Valid
        List<WordItem> words) {

  public record WordItem(
      @NotBlank(message = "Word cannot be blank") String word,
      @NotBlank(message = "Translation cannot be blank") String translation) {}
}
