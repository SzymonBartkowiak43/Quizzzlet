package com.example.quizlecikprojekt.domain.word.dto;

import com.example.quizlecikprojekt.domain.word.Word;

public class MapperWordToWordToRepeatDto {
    public WordToRepeatDto mapWordToWordToRepeatDto(Word word) {
        WordToRepeatDto wordToRepeatDto = new WordToRepeatDto();
        wordToRepeatDto.setWord(word.getWord());
        wordToRepeatDto.setTranslation(word.getTranslation());
        wordToRepeatDto.setCorrect(true);
        return wordToRepeatDto;
    }
}
