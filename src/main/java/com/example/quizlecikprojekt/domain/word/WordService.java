package com.example.quizlecikprojekt.domain.word;


import com.example.quizlecikprojekt.domain.user.UserRepository;
import com.example.quizlecikprojekt.domain.word.Dto.WordToRepeadDto;
import com.example.quizlecikprojekt.domain.word.Dto.MapperWordToWordToRepeadDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WordService {
    private final WordRepository wordRepository;
    private final MapperWordToWordToRepeadDto mapperWordToWordToRepeadDto;
    private final UserRepository userRepository;


    public WordService(WordRepository wordRepository, UserRepository userRepository) {
        this.wordRepository = wordRepository;
        this.userRepository = userRepository;
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
    public Long getUserIdByUsername(String username) {
        return userRepository.findByEmail(username).get().getId();
    }
}
