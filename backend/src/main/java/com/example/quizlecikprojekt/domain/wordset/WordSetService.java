package com.example.quizlecikprojekt.domain.wordset;

import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserRepository;
import com.example.quizlecikprojekt.domain.word.Word;
import com.example.quizlecikprojekt.domain.word.WordRepository;
import com.example.quizlecikprojekt.domain.wordset.exception.WordSetNotFoundException;
import com.example.quizlecikprojekt.domain.wordset.exception.WordSetOperationException;
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
public class WordSetService {

  private static final Logger logger = LoggerFactory.getLogger(WordSetService.class);

  private final WordSetRepository wordSetRepository;
  private final UserRepository userRepository;
  private final WordRepository wordRepository;
  private final WordSetTransactionalService transactionalService;

  public WordSetService(
      WordSetRepository wordSetRepository,
      UserRepository userRepository,
      WordRepository wordRepository,
      WordSetTransactionalService transactionalService) {
    this.wordSetRepository = wordSetRepository;
    this.userRepository = userRepository;
    this.wordRepository = wordRepository;
    this.transactionalService = transactionalService;
  }

  private static WordSet prepareWordSetForUpdate(
      WordSet wordSetForm, Optional<WordSet> wordSetOptional) {
    WordSet existingWordSet = wordSetOptional.get();

    if (wordSetForm.getTitle() != null) {
      existingWordSet.setTitle(wordSetForm.getTitle());
    }
    if (wordSetForm.getDescription() != null) {
      existingWordSet.setDescription(wordSetForm.getDescription());
    }
    if (wordSetForm.getLanguage() != null) {
      existingWordSet.setLanguage(wordSetForm.getLanguage());
    }
    if (wordSetForm.getTranslationLanguage() != null) {
      existingWordSet.setTranslationLanguage(wordSetForm.getTranslationLanguage());
    }
    return existingWordSet;
  }

  public List<WordSet> getWordSetsByEmail(String email) {
    Optional<User> userOptional = userRepository.getUserByEmail(email);
    if (userOptional.isEmpty()) {
      logger.warn("User not found with email: {}", email);
      return List.of();
    }

    User user = userOptional.get();
    return wordSetRepository.findByUser(Optional.of(user));
  }

  public List<Word> getWordsByWordSetId(Long wordSetId) {
    Optional<WordSet> wordSetOptional = wordSetRepository.findById(wordSetId);
    if (wordSetOptional.isEmpty()) {
      logger.warn("WordSet not found with id: {}", wordSetId);
      return List.of();
    }

    WordSet wordSet = wordSetOptional.get();
    return wordRepository.findByWordSet(Optional.of(wordSet));
  }

  public Optional<WordSet> getWordSetById(Long wordSetId) {
    return wordSetRepository.findById(wordSetId);
  }

  public boolean isWordSetOwnedByUser(Long wordSetId, String userEmail) {
    Optional<WordSet> wordSetOptional = getWordSetById(wordSetId);
    if (wordSetOptional.isEmpty()) {
      return false;
    }

    WordSet wordSet = wordSetOptional.get();
    return wordSet.getUser().getEmail().equals(userEmail);
  }

  public int getWordCountByWordSetId(Long wordSetId) {
    return getWordsByWordSetId(wordSetId).size();
  }

  public WordSet createWordSet(WordSet wordSet) {
    try {
      return transactionalService.createWordSet(wordSet);
    } catch (Exception e) {
      String userEmail = wordSet.getUser() != null ? wordSet.getUser().getEmail() : "unknown";
      logger.error("Failed to create WordSet for user: {}", userEmail, e);
      throw new WordSetOperationException("Failed to create WordSet for user: " + userEmail, e);
    }
  }

  public void deleteWordSet(Long id) {
    try {
      Optional<WordSet> wordSetOptional = getWordSetById(id);
      if (wordSetOptional.isEmpty()) {
        throw new WordSetNotFoundException("WordSet not found with id: " + id);
      }

      WordSet wordSet = wordSetOptional.get();
      logger.info(
          "Attempting to delete WordSet with id: {} for user: {}",
          id,
          wordSet.getUser().getEmail());

      transactionalService.deleteWordSet(id);

    } catch (WordSetNotFoundException e) {
      logger.error("WordSet not found for deletion with id: {}", id);
      throw e;
    } catch (Exception e) {
      logger.error("Error deleting WordSet with id: {}", id, e);
      throw new WordSetOperationException("Failed to delete WordSet with id: " + id, e);
    }
  }

  public WordSet updateWordSet(Long id, WordSet wordSetForm) {
    try {
      Optional<WordSet> wordSetOptional = getWordSetById(id);
      if (wordSetOptional.isEmpty()) {
        throw new WordSetNotFoundException("WordSet not found with id: " + id);
      }

      WordSet existingWordSet = prepareWordSetForUpdate(wordSetForm, wordSetOptional);

      if (wordSetForm.getWords() != null && !wordSetForm.getWords().isEmpty()) {
        updateWords(existingWordSet, wordSetForm.getWords());
      }

      WordSet updatedWordSet = transactionalService.updateWordSet(existingWordSet);
      logger.info("WordSet updated successfully with id: {}", id);
      return updatedWordSet;

    } catch (WordSetNotFoundException e) {
      logger.error("WordSet not found for update with id: {}", id);
      throw e;
    } catch (Exception e) {
      logger.error("Error updating WordSet with id: {}", id, e);
      throw new WordSetOperationException("Failed to update WordSet with id: " + id, e);
    }
  }

  private void updateWords(WordSet wordSet, List<Word> newWords) {
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

  public WordSet newWordSet(User user) {
    WordSet wordSet = new WordSet();
    wordSet.setUser(user);
    wordSet.setTitle("New Word Set");
    wordSet.setDescription("Description");
    wordSet.setLanguage("pl");
    wordSet.setTranslationLanguage("en");

    logger.debug("Created new WordSet template for user: {}", user.getEmail());
    return wordSet;
  }
}
