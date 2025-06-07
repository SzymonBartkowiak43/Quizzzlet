package com.example.quizlecikprojekt.domain;


import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserRepository;
import com.example.quizlecikprojekt.domain.word.Word;
import com.example.quizlecikprojekt.domain.word.WordRepository;
import com.example.quizlecikprojekt.domain.wordSet.WordSet;
import com.example.quizlecikprojekt.domain.wordSet.WordSetRepository;
import com.example.quizlecikprojekt.domain.wordSet.WordSetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class WordSetServiceTest {
    @Mock
    private WordSetRepository wordSetRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WordRepository wordRepository;

    @InjectMocks
    private WordSetService wordSetService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void newWordSetTest() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@wp.pl");
        user.setUserName("user");

        // When
        WordSet wordSet = wordSetService.newWordSet(user);

        // Then
        assertNotNull(wordSet);
    }


    @Test
    public void getWordSetsByEmailShouldReturnWordSetsWhenUserExistsTest() {
        // Given
        String email = "test@wp.pl";
        User user = new User();
        user.setEmail(email);

        WordSet wordSet1 = new WordSet();
        WordSet wordSet2 = new WordSet();

        List<WordSet> expectedWordSets = List.of(wordSet1, wordSet2);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(wordSetRepository.findByUser(Optional.of(user))).thenReturn(expectedWordSets);

        // When
        List<WordSet> actualWordSets = wordSetService.getWordSetsByEmail(email);

        // Then
        assertEquals(expectedWordSets, actualWordSets);
        verify(userRepository, times(1)).findByEmail(email);
        verify(wordSetRepository, times(1)).findByUser(Optional.of(user));
    }

    @Test
    public void getWordSetsByEmailShouldReturnEmptyListWhenUserNotFound() {
        // Given
        String email = "nonexistent@wp.pl";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(wordSetRepository.findByUser(Optional.empty())).thenReturn(Collections.emptyList());

        // When
        List<WordSet> actualWordSets = wordSetService.getWordSetsByEmail(email);

        // Then
        assertEquals(Collections.emptyList(), actualWordSets);
    }

    @Test
    public void getWordsByWordSetIdShoudReturnListOfWord() {
        // Given
        Word word1 = new Word();
        word1.setId(1L);
        Word word2 = new Word();
        word2.setId(2L);

        WordSet wordSet = new WordSet();
        wordSet.setWords(List.of(word1, word2));

        when(wordSetRepository.findById(1L)).thenReturn(Optional.of(wordSet));
        when(wordRepository.findByWordSet(Optional.of(wordSet))).thenReturn(List.of(word1, word2));

        // When
        List<Word> words = wordSetService.getWordsByWordSetId(1L);

        //Then
        assertEquals(2, words.size());
        assertEquals(word1, words.get(0));
        assertEquals(word2, words.get(1));

    }

}