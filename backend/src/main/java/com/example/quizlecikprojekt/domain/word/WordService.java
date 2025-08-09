package com.example.quizlecikprojekt.domain.word;

import com.example.quizlecikprojekt.domain.wordset.WordSet;
import jakarta.persistence.EntityNotFoundException;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WordService {
  private final WordRepository wordRepository;

  private static final Logger logger = LoggerFactory.getLogger(WordService.class);

  public WordService(WordRepository wordRepository) {
    this.wordRepository = wordRepository;
  }

  public void updateWordPoints(Long wordId, int newPoints) {
    Optional<Word> wordOptional = wordRepository.findById(wordId);
    if (wordOptional.isEmpty()) {
      throw new EntityNotFoundException("Word not found with id: " + wordId);
    }

    Word word = wordOptional.get();
    word.setPoints(newPoints);

    wordRepository.save(word);
  }

  public int deleteWords(List<Long> wordIds) {
    try {
      int deletedCount = 0;

      for (Long wordId : wordIds) {
        if (wordRepository.existsById(wordId)) {
          wordRepository.deleteById(wordId);
          deletedCount++;
        }
      }

      logger.info("Deleted {} words out of {} requested", deletedCount, wordIds.size());
      return deletedCount;

    } catch (Exception e) {
      logger.error("Error deleting multiple words: {}", wordIds, e);
      throw new RuntimeException("Failed to delete words", e);
    }
  }

  public void updateWords(WordSet wordSet, List<Word> newWords) {
    List<Word> existingWords = wordSet.getWords();

    Map<Long, Word> existingWordsMap =
            existingWords.stream().collect(Collectors.toMap(Word::getId, word -> word));

    for (Word formWord : newWords) {
      if (formWord.getId() != null && existingWordsMap.containsKey(formWord.getId())) {
        Word existingWord = existingWordsMap.get(formWord.getId());
        if (!existingWord.getWord().equals(formWord.getWord())
                || !existingWord.getTranslation().equals(formWord.getTranslation())) {
          existingWord.setWord(formWord.getWord());
          existingWord.setTranslation(formWord.getTranslation());
        }
      } else {
        formWord.setWordSet(wordSet);
        formWord.setPoints(0);
        formWord.setLastPracticed(Date.valueOf(LocalDateTime.now().toLocalDate()));
        wordSet.getWords().add(formWord);
      }
    }

    List<Long> formWordIds = newWords.stream().map(Word::getId).toList();
    wordSet
            .getWords()
            .removeIf(word -> word.getId() != null && !formWordIds.contains(word.getId()));
  }

  public Word updateWord(Long wordId, String newWord, String newTranslation) {
    Optional<Word> wordOptional = wordRepository.findById(wordId);
    if (wordOptional.isEmpty()) {
      throw new EntityNotFoundException("Word not found with id: " + wordId);
    }

    Word word = wordOptional.get();
    word.setWord(newWord.trim());
    word.setTranslation(newTranslation.trim());

    return wordRepository.save(word);
  }

  public void deleteWord(Long wordId) {
    try {
      if (!wordRepository.existsById(wordId)) {
        throw new EntityNotFoundException("Word not found with id: " + wordId);
      }

      wordRepository.deleteById(wordId);
      logger.info("Word deleted successfully with id: {}", wordId);

    } catch (EntityNotFoundException e) {
      logger.error("Word not found for deletion with id: {}", wordId);
      throw e;
    } catch (Exception e) {
      logger.error("Error deleting word with id: {}", wordId, e);
      throw new RuntimeException("Failed to delete word with id: " + wordId, e);
    }
  }
}
