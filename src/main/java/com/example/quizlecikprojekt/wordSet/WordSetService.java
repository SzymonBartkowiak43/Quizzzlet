package com.example.quizlecikprojekt.wordSet;


import com.example.quizlecikprojekt.user.User;
import com.example.quizlecikprojekt.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WordSetService {
    private final WordSetRepository wordSetRepository;
    private final UserRepository userRepository;

    public WordSetService(WordSetRepository wordSetRepository, UserRepository userRepository) {
        this.wordSetRepository = wordSetRepository;
        this.userRepository = userRepository;
    }

    public List<WordSet> getWordSetsByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return wordSetRepository.findByUser(user);
    }


}
