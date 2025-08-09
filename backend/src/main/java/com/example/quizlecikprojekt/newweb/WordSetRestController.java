package com.example.quizlecikprojekt.newweb;

import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserService;
import com.example.quizlecikprojekt.domain.word.Word;
import com.example.quizlecikprojekt.domain.word.WordService;
import com.example.quizlecikprojekt.domain.word.dto.WordAddRequest;
import com.example.quizlecikprojekt.domain.word.dto.WordAddResponse;
import com.example.quizlecikprojekt.domain.wordset.WordSet;
import com.example.quizlecikprojekt.domain.wordset.WordSetService;
import com.example.quizlecikprojekt.newweb.dto.word.WordDeleteRequest;
import com.example.quizlecikprojekt.newweb.dto.word.WordResponse;
import com.example.quizlecikprojekt.newweb.dto.wordset.WordSetCreateRequest;
import com.example.quizlecikprojekt.newweb.dto.wordset.WordSetResponse;
import com.example.quizlecikprojekt.newweb.dto.wordset.WordSetUpdateRequest;
import com.example.quizlecikprojekt.newweb.dto.wordset.WordUpdateRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/word-sets")
@CrossOrigin(origins = "http://localhost:3000")
public class WordSetRestController {

  private static final Logger logger = LoggerFactory.getLogger(WordSetRestController.class);

  private final WordSetService wordSetService;
  private final WordService wordService;
  private final UserService userService;

  public WordSetRestController(
      WordSetService wordSetService, WordService wordService, UserService userService) {
    this.wordSetService = wordSetService;
    this.wordService = wordService;
    this.userService = userService;
  }

  @PostMapping
  public ResponseEntity<?> createWordSet(
      Authentication authentication, @Valid @RequestBody WordSetCreateRequest request) {

    // owner
    User user = userService.getUserByEmail(authentication.getName());

    // create domain object
    WordSet wordSet = wordSetService.newWordSet(user);
    if (request.name() != null && !request.name().trim().isEmpty()) {
      wordSet.setTitle(request.name().trim());
    }
    if (request.description() != null) {
      wordSet.setDescription(request.description());
    }

    WordSet created = wordSetService.createWordSet(wordSet);

    WordSetResponse response = mapToWordSetResponse(created);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/{wordSetId}/words")
  public ResponseEntity<?> addWordsToWordSet(
      Authentication authentication,
      @PathVariable Long wordSetId,
      @Valid @RequestBody WordAddRequest request) {

    User user = userService.getUserByEmail(authentication.getName());

    try {
      List<Word> savedWords = wordSetService.addWordsToWordSet(wordSetId, request.words(), user);

      List<WordResponse> wordResponses = savedWords.stream().map(this::mapToWordResponse).toList();

      WordAddResponse response =
          new WordAddResponse(
              wordResponses,
              wordResponses.size(),
              wordResponses.size() == 1
                  ? "Word added successfully"
                  : wordResponses.size() + " words added successfully");

      return ResponseEntity.status(HttpStatus.CREATED).body(response);

    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    } catch (AccessDeniedException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }
  }

  @GetMapping
  @Transactional(readOnly = true)
  public ResponseEntity<List<WordSetResponse>> getWordSets(Authentication authentication) {
    User user = userService.getUserByEmail(authentication.getName());

    List<WordSet> wordSets = wordSetService.getWordSetsByUser(user);

    List<WordSetResponse> response = wordSets.stream().map(this::mapToWordSetResponse).toList();

    return ResponseEntity.ok(response);
  }

  @PutMapping("/{wordSetId}")
  public ResponseEntity<?> updateWordSet(
      Authentication authentication,
      @PathVariable Long wordSetId,
      @Valid @RequestBody WordSetUpdateRequest request) {

    User user = userService.getUserByEmail(authentication.getName());

    // Check if user owns the word set
    if (!wordSetService.isWordSetOwnedByUser(wordSetId, user.getEmail())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(Map.of("message", "You don't have permission to update this word set"));
    }

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
    WordSetResponse response = mapToWordSetResponse(updatedWordSet);

    return ResponseEntity.ok(response);
  }

  @PutMapping("/{wordSetId}/words/{wordId}")
  public ResponseEntity<?> updateWord(
      Authentication authentication,
      @PathVariable Long wordSetId,
      @PathVariable Long wordId,
      @Valid @RequestBody WordUpdateRequest request) {

    User user = userService.getUserByEmail(authentication.getName());

    try {
      // Check if user owns the word set
      if (!wordSetService.isWordSetOwnedByUser(wordSetId, user.getEmail())) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(Map.of("message", "You don't have permission to update this word"));
      }

      Word updatedWord = wordSetService.updateWord(wordId, request.word(), request.translation());
      WordResponse response = mapToWordResponse(updatedWord);

      return ResponseEntity.ok(response);

    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("message", "Failed to update word"));
    }
  }

  @DeleteMapping("/{wordSetId}")
  public ResponseEntity<?> deleteWordSet(
      Authentication authentication, @PathVariable Long wordSetId) {

    User user = userService.getUserByEmail(authentication.getName());

    // Check if user owns the word set
    if (!wordSetService.isWordSetOwnedByUser(wordSetId, user.getEmail())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(Map.of("message", "You don't have permission to delete this word set"));
    }

    wordSetService.deleteWordSet(wordSetId);

    return ResponseEntity.ok(
        Map.of("message", "Word set deleted successfully", "deletedWordSetId", wordSetId));
  }

  @DeleteMapping("/{wordSetId}/words/{wordId}")
  public ResponseEntity<?> deleteWord(
      Authentication authentication, @PathVariable Long wordSetId, @PathVariable Long wordId) {

    User user = userService.getUserByEmail(authentication.getName());

    // Check if user owns the word set
    if (!wordSetService.isWordSetOwnedByUser(wordSetId, user.getEmail())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(Map.of("message", "You don't have permission to delete this word"));
    }

    wordSetService.deleteWord(wordId);

    return ResponseEntity.ok(
        Map.of("message", "Word deleted successfully", "deletedWordId", wordId));
  }

  @DeleteMapping("/{wordSetId}/words")
  public ResponseEntity<?> deleteMultipleWords(
      Authentication authentication,
      @PathVariable Long wordSetId,
      @RequestBody WordDeleteRequest request) {

    User user = userService.getUserByEmail(authentication.getName());

    // Check if user owns the word set
    if (!wordSetService.isWordSetOwnedByUser(wordSetId, user.getEmail())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(Map.of("message", "You don't have permission to delete these words"));
    }

    int deletedCount = wordSetService.deleteWords(request.wordIds());

    return ResponseEntity.ok(
        Map.of(
            "message",
            deletedCount + " words deleted successfully",
            "deletedCount",
            deletedCount,
            "deletedWordIds",
            request.wordIds()));
  }

  private WordSetResponse mapToWordSetResponse(WordSet wordSet) {
    List<WordResponse> words = new ArrayList<>();

    if (wordSet.getWords() != null) {
      words = wordSet.getWords().stream().map(this::mapToWordResponse).toList();
    }

    return mapToWordSetResponse(wordSet, words);
  }

  private WordSetResponse mapToWordSetResponse(WordSet wordSet, List<WordResponse> wordResponses) {

    return WordSetResponse.builder()
        .id(wordSet.getId())
        .title(wordSet.getTitle())
        .description(wordSet.getDescription())
        .language(wordSet.getLanguage())
        .translationLanguage(wordSet.getTranslationLanguage())
        .createdAt(wordSet.getCreatedAt())
        .updatedAt(wordSet.getUpdatedAt())
        .words(wordResponses)
        .build();
  }

  private WordResponse mapToWordResponse(Word word) {
    return new WordResponse(
        word.getId(),
        word.getWord(),
        word.getTranslation(),
        word.getPoints(),
        word.isStar(),
        word.getLastPracticed(),
        word.getWordSet().getId());
  }
}
