package com.example.quizlecikprojekt.domain.word;

import com.example.quizlecikprojekt.domain.word.dto.WordToRepeatDto;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WordService {
  private final WordRepository wordRepository;

  public WordService(WordRepository wordRepository) {
    this.wordRepository = wordRepository;
  }

  public Word saveWord(Word word) {
    return wordRepository.save(word);
  }

  @Transactional
  public void deleteWordById(Long wordId) {
    wordRepository.deleteById(wordId);
  }

  public List<WordToRepeatDto> getCorrectWordsAndCreateUncoredWords(Long userId) {
    List<Word> wordsToRepeat = wordRepository.findWordsToRepeat(userId);
    List<WordToRepeatDto> uncorrectedWords = new ArrayList<>();
    for (int i = 0; i < wordsToRepeat.size(); i++) {
      try {
        int randomWord = (int) (Math.random() * wordsToRepeat.size());
        int randomTranslation = (int) (Math.random() * wordsToRepeat.size());
        if (randomWord != randomTranslation) {
          uncorrectedWords.add(
              new WordToRepeatDto(
                  wordsToRepeat.get(randomWord).getWord(),
                  wordsToRepeat.get(randomTranslation).getTranslation(),
                  false));
        }
      } catch (Exception e) {

      }
    }
    return uncorrectedWords;
  }

  // Add this method to your WordService
  public Word updateWordPoints(Long wordId, int newPoints) {
    Optional<Word> wordOptional = wordRepository.findById(wordId);
    if (wordOptional.isEmpty()) {
      throw new EntityNotFoundException("Word not found with id: " + wordId);
    }

    Word word = wordOptional.get();
    word.setPoints(newPoints);

    return wordRepository.save(word);
  }

  public List<WordToRepeatDto> getWordsToRepeat(Long userId) {
    try {
      List<Word> wordsToRepeat = wordRepository.findWordsToRepeat(userId);

      if (wordsToRepeat.isEmpty()) {
        return List.of();
      }

      List<WordToRepeatDto> correctWords = new ArrayList<>();

      for (Word word : wordsToRepeat) {
        word.setLastPracticed(Date.valueOf(LocalDate.now()));

        correctWords.add(new WordToRepeatDto(word.getWord(), word.getTranslation(), true));
      }

      wordRepository.saveAll(wordsToRepeat);

      return correctWords;

    } catch (Exception e) {
      return List.of();
    }
  }
}
