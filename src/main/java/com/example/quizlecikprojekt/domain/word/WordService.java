package com.example.quizlecikprojekt.domain.word;


import com.example.quizlecikprojekt.domain.word.Dto.WordToRepeadDto;
import com.example.quizlecikprojekt.domain.word.Dto.MapperWordToWordToRepeadDto;
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
    private final static Logger LOGGER = LoggerFactory.getLogger(WordService.class);


    public WordService(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
        mapperWordToWordToRepeadDto = new MapperWordToWordToRepeadDto();
    }

    public void saveWord(Word word) {
        LOGGER.info("Entering saveWord with word: {}", word);
        wordRepository.save(word);
        LOGGER.info("Word saved successfully");
    }

    @Transactional
    public void deleteWordById(Long wordId) {
        LOGGER.info("Entering deleteWordById with wordId: {}", wordId);
        wordRepository.deleteById(wordId);
        LOGGER.info("Word deleted successfully");
    }

    public Word getWordById(Long wordId) {
        LOGGER.info("Entering getWordById with wordId: {}", wordId);
        return wordRepository.findById(wordId).orElse(null);
    }

    public List<WordToRepeadDto> getWordsToRepeat(Long userId) {
        LOGGER.info("Entering getWordsToRepeat with userId: {}", userId);
        List<Word> wordsToRepeat = wordRepository.findWordsToRepeat(userId);

        List<WordToRepeadDto> collect = wordsToRepeat.stream()
                .map(mapperWordToWordToRepeadDto::mapWordToWordToRepeadDto)
                .toList();
        List<WordToRepeadDto> unccorectWords = getCorrectWordsAndCreateUncoredWords(userId);

        List<WordToRepeadDto> allWords = new ArrayList<>();
        allWords.addAll(collect);
        allWords.addAll(unccorectWords);

        LOGGER.info("Returning words to repeat");
        return allWords;
    }

    public List<WordToRepeadDto> getCorrectWordsAndCreateUncoredWords(Long userId) {
        LOGGER.info("Entering getCorrectWordsAndCreateUncoredWords with userId: {}", userId);
        List<Word> wordsToRepeat = wordRepository.findWordsToRepeat(userId);
        List<WordToRepeadDto> unccorectWords = new ArrayList<>();
        for(int i = 0; i < wordsToRepeat.size(); i++) {
            try {
                int randomWord = (int) (Math.random() * wordsToRepeat.size());
                int randomTranzlation = (int) (Math.random() * wordsToRepeat.size());
                if(randomWord != randomTranzlation) {
                    unccorectWords.add(new WordToRepeadDto(wordsToRepeat.get(randomWord).getWord(), wordsToRepeat.get(randomTranzlation).getTranslation(), false));
                }
            } catch (Exception e) {
                LOGGER.error("Error in getCorrectWordsAndCreateUncoredWords: {}", e.getMessage());
            }
        }
        LOGGER.info("Returning incorrect words");
        return unccorectWords;
    }

}
