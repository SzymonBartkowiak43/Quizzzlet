package com.example.quizlecikprojekt.domain.wordset;

import com.example.quizlecikprojekt.controllers.dto.wordset.WordSetCreateRequest;
import com.example.quizlecikprojekt.entity.User;
import com.example.quizlecikprojekt.entity.Word;
import com.example.quizlecikprojekt.domain.wordset.dto.WordItem;
import com.example.quizlecikprojekt.exception.WordSetNotFoundException;
import com.example.quizlecikprojekt.exception.WordSetOperationException;
import java.util.List;

import com.example.quizlecikprojekt.entity.WordSet;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
class WordSetService {

  private final WordSetRepository wordSetRepository;
  private final WordRepository wordRepository;
  private final WordFacade wordFacade;
  private final WordSetTransactionalService transactionalService;

  public WordSet getWordSetById(Long wordSetId) {
    return wordSetRepository
        .findById(wordSetId)
        .orElseThrow(() -> new WordSetNotFoundException("WordSet not found with id: " + wordSetId));
  }

  public void deleteWordSet(Long id) {
    try {
      transactionalService.deleteWordSet(id);

    } catch (WordSetNotFoundException e) {
      throw e;
    } catch (Exception e) {
      throw new WordSetOperationException("Failed to delete WordSet with id: " + id, e);
    }
  }

  public WordSet updateWordSet(Long id, WordSet wordSetForm) {
    try {
      WordSet wordSetOptional = getWordSetById(id);

      WordSet existingWordSet = prepareWordSetForUpdate(wordSetForm, wordSetOptional);

      if (wordSetForm.getWords() != null && !wordSetForm.getWords().isEmpty()) {
        wordFacade.updateWords(existingWordSet, wordSetForm.getWords());
      }

      return transactionalService.updateWordSet(existingWordSet);

    } catch (WordSetNotFoundException e) {
      throw e;
    } catch (Exception e) {
      throw new WordSetOperationException("Failed to update WordSet with id: " + id, e);
    }
  }

  @Transactional
  public List<Word> addWordsToWordSet(Long wordSetId, List<WordItem> wordItems, User user) {
    WordSet wordSet = getWordSetById(wordSetId);

    if (!wordSet.getUser().equals(user)) {
      throw new AccessDeniedException("You don't have permission to modify this word set");
    }

    List<Word> wordsToSave =
        wordItems.stream()
            .map(
                wordItem -> {
                  Word word = new Word();
                  word.setWord(wordItem.word().trim());
                  word.setTranslation(wordItem.translation().trim());
                  word.setPoints(0);
                  word.setStar(false);
                  word.setWordSet(wordSet);
                  return word;
                })
            .toList();

    return wordRepository.saveAll(wordsToSave);
  }

  public List<WordSet> getWordSetsByUser(User user) {
    List<WordSet> wordSets = wordSetRepository.findByUserWithWordsOrderByCreatedAtDesc(user);

    for (WordSet wordSet : wordSets) {
      wordSet.getWords().size();
    }

    return wordSets;
  }

  private static WordSet prepareWordSetForUpdate(WordSet wordSetForm, WordSet existingWordSet) {

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

  public WordSet createWordSet(User user, WordSetCreateRequest request) {
    WordSet wordSet = new WordSet();
    wordSet.setUser(user);
    wordSet.setTitle(request.name() != null ? request.name().trim() : "New Word Set");
    wordSet.setDescription(request.description() != null ? request.description() : "Description");
    wordSet.setLanguage("pl");
    wordSet.setTranslationLanguage("en");

    return transactionalService.createWordSet(wordSet);
  }
}
