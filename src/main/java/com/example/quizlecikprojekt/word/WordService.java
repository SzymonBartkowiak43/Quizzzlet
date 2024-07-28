package com.example.quizlecikprojekt.word;


import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WordService {
    private final WordRepository wordRepository;
    private final int MAX_REPEAT = 10;

    public WordService(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    public void saveWord(Word word) {
        wordRepository.save(word);
    }

    public void deleteWordById(Long wordId) {
        wordRepository.deleteById(wordId);
    }

    public Word getWordById(Long wordId) {
        return wordRepository.findById(wordId).orElse(null);
    }

    public List<Word> getWordsToRepeat() {
        return wordRepository.findWordsToRepeat(MAX_REPEAT);
    }
}
