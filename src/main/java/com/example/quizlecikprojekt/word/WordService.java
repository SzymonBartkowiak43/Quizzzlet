package com.example.quizlecikprojekt.word;


import org.springframework.stereotype.Service;

@Service
public class WordService {
    private final WordRepository wordRepository;

    public WordService(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    public void saveWord(Word word) {
        wordRepository.save(word);
    }

    public void deleteWordById(Long wordId) {
        wordRepository.deleteById(wordId);
    }
}
