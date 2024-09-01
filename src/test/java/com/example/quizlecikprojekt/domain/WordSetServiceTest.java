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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WordSetServiceTest {
    @Mock
    private  WordSetRepository wordSetRepository;

    @Mock
    private  UserRepository userRepository;

    @Mock
    private  WordRepository wordRepository;

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
    public void mapExistingWordsReturnCorrectMapTest() {
        // Given
        WordSet wordSet = new WordSet();

        Word word1 = new Word();
        word1.setId(1L);
        word1.setWord("word1");

        Word word2 = new Word();
        word2.setId(2L);
        word2.setWord("word2");

        wordSet.setWords(List.of(word1, word2));

        // When
        Map<Long, Word> wordMap = wordSetService.mapExistingWords(wordSet);

        // Then
        assertEquals(2, wordMap.size(), "Map should contain 2 entries");
    }

    @Test
    public void mapExistingWordsShouldReturnEmptyTest() {
        // Given
        WordSet wordSet = new WordSet();
        wordSet.setWords(List.of());

        // When
        Map<Long, Word> wordMap = wordSetService.mapExistingWords(wordSet);

        // Then
        assertEquals(0, wordMap.size(), "Map should be empty");
    }

    @Test
    public void updateOrAddWordsShouldUpdateExistingWordsTest() {
        // Given
        WordSet existingWordSet = new WordSet();
        WordSet updatedWordSet = new WordSet();

        Word word1 = new Word();
        word1.setId(1L);
        word1.setWord("word1");
        word1.setTranslation("translation1");

        Word word2 = new Word();
        word2.setId(1L);
        word2.setWord("newWord1");
        word2.setTranslation("newTranslation1");

        existingWordSet.setWords(new ArrayList<>(List.of(word1)));
        updatedWordSet.setWords(new ArrayList<>(List.of(word2)));

        Map<Long, Word> existingWordsMap = new HashMap<>();
        existingWordsMap.put(1L, word1);

        // When
        wordSetService.updateOrAddWords(updatedWordSet, existingWordSet, existingWordsMap);

        // Then
        assertEquals(1, existingWordSet.getWords().size());
        assertEquals("newWord1", word1.getWord());
        assertEquals("newTranslation1", word1.getTranslation());
    }

    @Test
    public void updateOrAddWordsShouldAddNewWordsTest() {
        // Given
        WordSet existingWordSet = new WordSet();
        WordSet updatedWordSet = new WordSet();

        Word newWord = new Word();
        newWord.setId(2L);
        newWord.setWord("newWord");
        newWord.setTranslation("newTranslation");

        existingWordSet.setWords(new ArrayList<>());
        updatedWordSet.setWords(new ArrayList<>(List.of(newWord)));

        Map<Long, Word> existingWordsMap = new HashMap<>();

        // When
        wordSetService.updateOrAddWords(updatedWordSet, existingWordSet, existingWordsMap);

        Word addedWord = existingWordSet.getWords().get(0);
        // Then
        assertEquals(1, existingWordSet.getWords().size());
        assertEquals("newWord", addedWord.getWord());
        assertEquals("newTranslation", addedWord.getTranslation());
    }

    @Test
    public void updateOrAddWordsShouldNotUpdateIfNoChanges() {
        // Given
        WordSet existingWordSet = new WordSet();
        WordSet updatedWordSet = new WordSet();

        Word word1 = new Word();
        word1.setId(1L);
        word1.setWord("word1");
        word1.setTranslation("Translation1");

        Word word2 = new Word();
        word2.setId(1L);
        word2.setWord("word1");
        word2.setTranslation("existingTranslation1");

        existingWordSet.setWords(new ArrayList<>(List.of(word1)));
        updatedWordSet.setWords(new ArrayList<>(List.of(word2)));

        Map<Long, Word> existingWordsMap = new HashMap<>();
        existingWordsMap.put(1L, word1);

        // When
        wordSetService.updateOrAddWords(updatedWordSet, existingWordSet, existingWordsMap);

        // Then
        assertEquals(1, existingWordSet.getWords().size());
        assertEquals("word1", word1.getWord());
        assertEquals("existingTranslation1", word1.getTranslation());
    }

    @Test
    public void updateOrAddWordsShouldUpdateAndAddWordsTest() {
        // Given
        WordSet existingWordSet = new WordSet();
        WordSet updatedWordSet = new WordSet();

        Word word1 = new Word();
        word1.setId(1L);
        word1.setWord("word1");
        word1.setTranslation("translation1");

        Word word2 = new Word();
        word2.setId(1L);
        word2.setWord("word1");
        word2.setTranslation("translation2");

        Word word3 = new Word();
        word3.setId(3L);
        word3.setWord("word3");
        word3.setTranslation("translation3");


        existingWordSet.setWords(new ArrayList<>(List.of(word1)));
        updatedWordSet.setWords(new ArrayList<>(List.of(word2, word3)));

        Map<Long, Word> existingWordsMap = new HashMap<>();
        existingWordsMap.put(1L, word1);

        // When
        wordSetService.updateOrAddWords(updatedWordSet, existingWordSet, existingWordsMap);

        // Then
        assertEquals(2, existingWordSet.getWords().size());
        assertTrue(existingWordSet.getWords().contains(word3));
        assertTrue(existingWordSet.getWords().stream().anyMatch((word) -> word.getTranslation().equals("translation2")));
        assertFalse(existingWordSet.getWords().stream().anyMatch((word) -> word.getTranslation().equals("translation1")));
    }

    @Test
    public void removeDeletedWordsShouldRemoveWordsNotInUpdatedWordSetTest() {
        // Given
        WordSet existingWordSet = new WordSet();
        WordSet updatedWordSet = new WordSet();

        Word word1 = new Word();
        word1.setId(1L);
        Word word2 = new Word();
        word2.setId(2L);
        Word word3 = new Word();
        word3.setId(3L);

        existingWordSet.setWords(new ArrayList<>(List.of(word1, word2, word3)));
        updatedWordSet.setWords(new ArrayList<>(List.of(word1, word3)));

        // When
        wordSetService.removeDeletedWords(updatedWordSet, existingWordSet);

        // Then
        assertEquals(2, existingWordSet.getWords().size());
        assertTrue(existingWordSet.getWords().contains(word1));
        assertTrue(existingWordSet.getWords().contains(word3));
        assertTrue(existingWordSet.getWords().stream().noneMatch(word -> word.getId().equals(2L)));
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