package com.example.quizlecikprojekt.domain;

import com.example.quizlecikprojekt.domain.word.Word;
import com.example.quizlecikprojekt.domain.word.WordRepository;
import com.example.quizlecikprojekt.domain.word.WordService;
import com.example.quizlecikprojekt.domain.word.dto.MapperWordToWordToRepeadDto;
import com.example.quizlecikprojekt.domain.word.dto.WordToRepeadDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WordServiceTest {

    @Mock
    private WordRepository wordRepository;

    @Mock
    private MapperWordToWordToRepeadDto mapperWordToWordToRepeadDto;

    @InjectMocks
    private WordService wordService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getCorrectWordsAndCreateUncoredWordsShouldReturnEmptyListWhenNoWordsToRepeat() {
        // Given
        Long userId = 1L;
        when(wordRepository.findWordsToRepeat(userId)).thenReturn(new ArrayList<>());

        // When
        List<WordToRepeadDto> result = wordService.getCorrectWordsAndCreateUncoredWords(userId);

        // Then
        assertEquals(0, result.size());
        verify(wordRepository, times(1)).findWordsToRepeat(userId);
    }

    @Test
    public void getCorrectWordsAndCreateUncoredWordsShouldGenerateUncorrectWords() {
        // Given
        Long userId = 1L;
        Word word1 = new Word();
        word1.setId(1L);
        word1.setWord("Word1");
        word1.setTranslation("Translation1");

        Word word2 = new Word();
        word2.setId(2L);
        word2.setWord("Word2");
        word2.setTranslation("Translation2");

        Word word3 = new Word();
        word3.setId(3L);
        word3.setWord("Word3");
        word3.setTranslation("Translation3");

        List<Word> wordsToRepeat = List.of(word1, word2, word3);
        when(wordRepository.findWordsToRepeat(userId)).thenReturn(wordsToRepeat);

        // When
        List<WordToRepeadDto> result = wordService.getCorrectWordsAndCreateUncoredWords(userId);

        // Then
        for (WordToRepeadDto dto : result) {
            assertFalse(dto.isCorrect());
            assertTrue(wordsToRepeat.stream().anyMatch(word -> word.getWord().equals(dto.getWord())));
            assertTrue(wordsToRepeat.stream().anyMatch(word -> word.getTranslation().equals(dto.getTranslation())));
            for (Word word : wordsToRepeat) {
                assertFalse(word.getWord().equals(dto.getWord()) && word.getTranslation().equals(dto.getTranslation()));
            }

        }
    }


}
