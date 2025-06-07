package com.example.quizlecikprojekt.domain.word;


import com.example.quizlecikprojekt.domain.word.dto.MapperWordToWordToRepeadDto;
import com.example.quizlecikprojekt.domain.word.dto.WordToRepeadDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class WordService {
    private final WordRepository wordRepository;
    private final MapperWordToWordToRepeadDto mapperWordToWordToRepeadDto;


    public WordService(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
        mapperWordToWordToRepeadDto = new MapperWordToWordToRepeadDto();
    }

    public void saveWord(Word word) {
        wordRepository.save(word);
    }

    @Transactional
    public void deleteWordById(Long wordId) {
        wordRepository.deleteById(wordId);
    }

    public Word getWordById(Long wordId) {
        return wordRepository.findById(wordId).orElse(null);
    }

    public List<WordToRepeadDto> getWordsToRepeat(Long userId) {
        List<Word> wordsToRepeat = wordRepository.findWordsToRepeat(userId);

        List<WordToRepeadDto> collect = wordsToRepeat.stream()
                .map(mapperWordToWordToRepeadDto::mapWordToWordToRepeadDto)
                .toList();
        List<WordToRepeadDto> unccorectWords = getCorrectWordsAndCreateUncoredWords(userId);

        List<WordToRepeadDto> allWords = new ArrayList<>();
        allWords.addAll(collect);
        allWords.addAll(unccorectWords);

        return allWords;
    }

    public List<WordToRepeadDto> getCorrectWordsAndCreateUncoredWords(Long userId) {
        List<Word> wordsToRepeat = wordRepository.findWordsToRepeat(userId);
        List<WordToRepeadDto> unccorectWords = new ArrayList<>();
        for (int i = 0; i < wordsToRepeat.size(); i++) {
            try {
                int randomWord = (int) (Math.random() * wordsToRepeat.size());
                int randomTranzlation = (int) (Math.random() * wordsToRepeat.size());
                if (randomWord != randomTranzlation) {
                    unccorectWords.add(new WordToRepeadDto(wordsToRepeat.get(randomWord).getWord(), wordsToRepeat.get(randomTranzlation).getTranslation(), false));
                }
            } catch (Exception e) {

            }
        }
        return unccorectWords;
    }

}
