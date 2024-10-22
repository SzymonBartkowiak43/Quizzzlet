package com.example.quizlecikprojekt.domain.word.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class WordToRepeadDto {
    private String word;
    private String translation;
    private boolean isCorrect;

    public WordToRepeadDto(String word, String translation, boolean isCorrect) {
        this.word = word;
        this.translation = translation;
        this.isCorrect = isCorrect;
    }
    public WordToRepeadDto() {

    }
}
