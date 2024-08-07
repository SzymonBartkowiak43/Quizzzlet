package com.example.quizlecikprojekt.domain.wordSet;


import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserRepository;
import com.example.quizlecikprojekt.domain.word.Word;
import com.example.quizlecikprojekt.domain.word.WordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WordSetService {
    private final WordSetRepository wordSetRepository;
    private final UserRepository userRepository;
    private final WordRepository wordRepository;

    public WordSetService(WordSetRepository wordSetRepository, UserRepository userRepository, WordRepository wordRepository) {
        this.wordSetRepository = wordSetRepository;
        this.userRepository = userRepository;
        this.wordRepository = wordRepository;
    }

    public List<WordSet> getWordSetsByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return wordSetRepository.findByUser(user);
    }

    public List<Word> getWordsByWordSetId(Long wordSetId) {
        Optional<WordSet> wordSet = wordSetRepository.findById(wordSetId);
        return wordRepository.findByWordSet(wordSet);
    }

    public Optional<WordSet> getWordSetById(Long wordSetId) {
        return wordSetRepository.findById(wordSetId);
    }



    @Transactional
    public void saveWordSet(WordSet wordSet) {
        wordSetRepository.save(wordSet);
    }

    @Transactional
    public void createWordSet(WordSet wordSet) {
        wordSetRepository.save(wordSet);
    }
    @Transactional
    public void deleteWordSet(Long id) {
        wordSetRepository.deleteById(id);
    }



}
