package com.example.quizlecikprojekt.domain.wordset;

import static com.example.quizlecikprojekt.domain.wordset.WordSetMapper.mapToWordResponse;
import static com.example.quizlecikprojekt.domain.wordset.WordSetMapper.mapToWordSetResponse;

import com.example.quizlecikprojekt.controllers.dto.word.WordResponse;
import com.example.quizlecikprojekt.controllers.dto.wordset.WordSetCreateRequest;
import com.example.quizlecikprojekt.controllers.dto.wordset.WordSetResponse;
import com.example.quizlecikprojekt.controllers.dto.wordset.WordSetUpdateRequest;
import com.example.quizlecikprojekt.controllers.dto.wordset.WordUpdateRequest;
import com.example.quizlecikprojekt.entity.User;
import com.example.quizlecikprojekt.domain.user.UserFacade;
import com.example.quizlecikprojekt.entity.Word;
import com.example.quizlecikprojekt.domain.wordset.dto.WordAddRequest;
import com.example.quizlecikprojekt.domain.wordset.dto.WordAddResponse;
import java.util.List;

import com.example.quizlecikprojekt.entity.WordSet;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class WordSetFacade {

  private final WordSetService wordSetService;
  private final WordFacade wordFacade;
  private final UserFacade userFacade;

  public WordSetResponse createNewWordSet(String userEmail, WordSetCreateRequest request) {
    User user = userFacade.getUserByEmail(userEmail);

    // Tutaj przekazujemy request (z językami) do serwisu
    WordSet created = wordSetService.createWordSet(user, request);

    return mapToWordSetResponse(created);
  }

  public WordAddResponse addWordsToWordSet(
          String userEmail, Long wordSetId, WordAddRequest request) {

    User user = userFacade.getUserByEmail(userEmail);

    List<Word> savedWords = wordSetService.addWordsToWordSet(wordSetId, request.words(), user);

    List<WordResponse> wordResponses =
            savedWords.stream().map(WordSetMapper::mapToWordResponse).toList();

    return new WordAddResponse(
            wordResponses,
            wordResponses.size(),
            wordResponses.size() == 1
                    ? "Word added successfully"
                    : wordResponses.size() + " words added successfully");
  }

  public List<WordSetResponse> getWordSets(String userEmail) {
    User user = userFacade.getUserByEmail(userEmail);
    List<WordSet> wordSets = wordSetService.getWordSetsByUser(user);
    return wordSets.stream().map(WordSetMapper::mapToWordSetResponse).toList();
  }

  public WordSetResponse updateWordSet(
          String userEmail, Long wordSetId, WordSetUpdateRequest request) {

    WordSet wordSetForm = new WordSet();
    if (request.title() != null) {
      wordSetForm.setTitle(request.title().trim());
    }
    if (request.description() != null) {
      wordSetForm.setDescription(request.description());
    }
    if (request.language() != null) {
      wordSetForm.setLanguage(request.language());
    }
    if (request.translationLanguage() != null) {
      wordSetForm.setTranslationLanguage(request.translationLanguage());
    }

    WordSet updatedWordSet = wordSetService.updateWordSet(wordSetId, wordSetForm);

    return mapToWordSetResponse(updatedWordSet);
  }

  public WordResponse updateWord(
          String userEmail, Long wordSetId, Long wordId, WordUpdateRequest request) {
    Word updatedWord = wordFacade.updateWord(wordId, request.word(), request.translation());
    return mapToWordResponse(updatedWord);
  }

  public void deleteWordSet(String name, Long wordSetId) {
    wordSetService.deleteWordSet(wordSetId);
  }

  public void deleteWord(String name, Long wordSetId, Long wordId) {
    wordFacade.deleteWord(wordId);
  }

  public int deleteWords(String name, Long wordSetId, List<Long> longs) {
    return wordFacade.deleteWords(longs);
  }

  public WordSet getWordSetById(Long wordSetId) {
    return wordSetService.getWordSetById(wordSetId);
  }
}