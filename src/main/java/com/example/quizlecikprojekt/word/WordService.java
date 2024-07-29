package com.example.quizlecikprojekt.word;


import com.example.quizlecikprojekt.word.Dto.MapperWordToWordToRepeadDto;
import com.example.quizlecikprojekt.word.Dto.WordToRepeadDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WordService {
    private final WordRepository wordRepository;
    private MapperWordToWordToRepeadDto mapperWordToWordToRepeadDto;


    public WordService(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
        mapperWordToWordToRepeadDto = new MapperWordToWordToRepeadDto();
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

    public List<WordToRepeadDto> getWordsToRepeat() {
        List<Word> wordsToRepeat = wordRepository.findWordsToRepeat();

        List<WordToRepeadDto> collect = wordsToRepeat.stream()
                .map(mapperWordToWordToRepeadDto::mapWordToWordToRepeadDto)
                .toList();
        List<WordToRepeadDto> unccorectWords = getCorrectWordsAndCreateUncoredWords();

        List<WordToRepeadDto> allWords = new ArrayList<>();
        allWords.addAll(collect);
        allWords.addAll(unccorectWords);

        return allWords;
    }

    public List<WordToRepeadDto> getCorrectWordsAndCreateUncoredWords() {
        List<Word> wordsToRepeat = wordRepository.findWordsToRepeat();
        List<WordToRepeadDto> unccorectWords = new ArrayList<>();
        for(int i = 0; i < wordsToRepeat.size(); i++) {
            try {
                int randomWord = (int) (Math.random() * wordsToRepeat.size());
                int randomTranzlation = (int) (Math.random() * wordsToRepeat.size());
                if(randomWord != randomTranzlation) {
                    unccorectWords.add(new WordToRepeadDto(wordsToRepeat.get(randomWord).getWord(), wordsToRepeat.get(randomTranzlation).getTranslation(), false));
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        return unccorectWords;
    }
}
