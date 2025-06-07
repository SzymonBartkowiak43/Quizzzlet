package com.example.quizlecikprojekt.domain.word.dto;

import com.example.quizlecikprojekt.domain.word.Word;

public class MapperWordToWordToRepeadDto {
    public WordToRepeadDto mapWordToWordToRepeadDto(Word word) {
        WordToRepeadDto wordToRepeadDto = new WordToRepeadDto();
        wordToRepeadDto.setWord(word.getWord());
        wordToRepeadDto.setTranslation(word.getTranslation());
        wordToRepeadDto.setCorrect(true);
        return wordToRepeadDto;
    }
}
