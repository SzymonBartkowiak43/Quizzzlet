package com.example.quizlecikprojekt.newweb;

import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserService;
import com.example.quizlecikprojekt.domain.word.Word;
import com.example.quizlecikprojekt.domain.word.WordService;
import com.example.quizlecikprojekt.domain.word.dto.WordAddRequest;
import com.example.quizlecikprojekt.domain.word.dto.WordAddResponse;
import com.example.quizlecikprojekt.domain.wordset.WordSet;
import com.example.quizlecikprojekt.domain.wordset.WordSetService;
import com.example.quizlecikprojekt.newweb.dto.word.WordResponse;
import com.example.quizlecikprojekt.newweb.dto.wordset.WordSetCreateRequest;
import com.example.quizlecikprojekt.newweb.dto.wordset.WordSetResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
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

  //
  //  @DeleteMapping("/{id}")
  //  public ResponseEntity<ApiResponse<String>> deleteWordSet(
  //      @PathVariable Long id, Authentication authentication) {
  //    try {
  //      if (authentication == null || !authentication.isAuthenticated()) {
  //        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
  //            .body(ApiResponse.error("User not authenticated"));
  //      }
  //
  //      Optional<WordSet> wordSetOptional = wordSetService.getWordSetById(id);
  //      if (wordSetOptional.isEmpty()) {
  //        return ResponseEntity.status(HttpStatus.NOT_FOUND)
  //            .body(ApiResponse.error("Word set not found"));
  //      }
  //
  //      String userEmail = authentication.getName();
  //
  //      if (wordSetService.isWordSetOwnedByUser(id, userEmail)) {
  //        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Access
  // denied"));
  //      }
  //
  //      wordSetService.deleteWordSet(id);
  //
  //      logger.info("Word set {} deleted by user: {}", id, userEmail);
  //      return ResponseEntity.ok(ApiResponse.success("Word set deleted successfully", "Deleted"));
  //
  //    } catch (Exception e) {
  //      logger.error("Error deleting word set {} for user: {}", id, authentication.getName(), e);
  //      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
  //          .body(ApiResponse.error("Failed to delete word set"));
  //    }
  //  }
  //
  //  @GetMapping("/{id}")
  //  public ResponseEntity<ApiResponse<WordSetResponse>> getWordSetWithWords(
  //      @PathVariable Long id, Authentication authentication) {
  //    try {
  //      if (authentication == null || !authentication.isAuthenticated()) {
  //        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
  //            .body(ApiResponse.error("User not authenticated"));
  //      }
  //
  //      Optional<WordSet> wordSetOptional = wordSetService.getWordSetById(id);
  //      if (wordSetOptional.isEmpty()) {
  //        return ResponseEntity.status(HttpStatus.NOT_FOUND)
  //            .body(ApiResponse.error("Word set not found"));
  //      }
  //
  //      WordSet wordSet = wordSetOptional.get();
  //      String userEmail = authentication.getName();
  //
  //      if (wordSetService.isWordSetOwnedByUser(id, userEmail)) {
  //        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Access
  // denied"));
  //      }
  //
  //      List<Word> words = wordSetService.getWordsByWordSetId(id);
  //      WordSetResponse response = mapToWordSetResponse(wordSet, words);
  //
  //      return ResponseEntity.ok(ApiResponse.success("Word set retrieved successfully",
  // response));
  //
  //    } catch (Exception e) {
  //      logger.error("Error retrieving word set {} for user: {}", id, authentication.getName(),
  // e);
  //      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
  //          .body(ApiResponse.error("Failed to retrieve word set"));
  //    }
  //  }
  //
  //  @PutMapping("/{id}")
  //  public ResponseEntity<ApiResponse<WordSetResponse>> updateWordSet(
  //      @PathVariable Long id,
  //      @Valid @RequestBody WordSetUpdateRequest request,
  //      Authentication authentication) {
  //    try {
  //      if (authentication == null || !authentication.isAuthenticated()) {
  //        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
  //            .body(ApiResponse.error("User not authenticated"));
  //      }
  //
  //      Optional<WordSet> wordSetOptional = wordSetService.getWordSetById(id);
  //      if (wordSetOptional.isEmpty()) {
  //        return ResponseEntity.status(HttpStatus.NOT_FOUND)
  //            .body(ApiResponse.error("Word set not found"));
  //      }
  //
  //      String userEmail = authentication.getName();
  //      if (wordSetService.isWordSetOwnedByUser(id, userEmail)) {
  //        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Access
  // denied"));
  //      }
  //
  //      WordSet wordSetForm = new WordSet();
  //      wordSetForm.setTitle(request.name());
  //      wordSetForm.setDescription(request.description());
  //
  //      WordSet updatedWordSet = wordSetService.updateWordSet(id, wordSetForm);
  //      WordSetResponse response = mapToWordSetResponse(updatedWordSet);
  //
  //      logger.info("Word set {} updated by user: {}", id, userEmail);
  //      return ResponseEntity.ok(ApiResponse.success("Word set updated successfully", response));
  //
  //    } catch (Exception e) {
  //      logger.error("Error updating word set {} for user: {}", id, authentication.getName(), e);
  //      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
  //          .body(ApiResponse.error("Failed to update word set"));
  //    }
  //  }

  //  @DeleteMapping("/{id}/words/{wordId}")
  //  public ResponseEntity<ApiResponse<String>> deleteWordFromWordSet(
  //      @PathVariable Long id, @PathVariable Long wordId, Authentication authentication) {
  //    try {
  //      if (authentication == null || !authentication.isAuthenticated()) {
  //        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
  //            .body(ApiResponse.error("User not authenticated"));
  //      }
  //
  //      Optional<WordSet> wordSetOptional = wordSetService.getWordSetById(id);
  //      if (wordSetOptional.isEmpty()) {
  //        return ResponseEntity.status(HttpStatus.NOT_FOUND)
  //            .body(ApiResponse.error("Word set not found"));
  //      }
  //
  //      String userEmail = authentication.getName();
  //      if (wordSetService.isWordSetOwnedByUser(id, userEmail)) {
  //        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Access
  // denied"));
  //      }
  //
  //      wordService.deleteWordById(wordId);
  //
  //      logger.info("Word {} deleted from word set {} by user: {}", wordId, id, userEmail);
  //      return ResponseEntity.ok(ApiResponse.success("Word deleted successfully", "Deleted"));
  //
  //    } catch (Exception e) {
  //      logger.error(
  //          "Error deleting word {} from word set {} for user: {}",
  //          wordId,
  //          id,
  //          authentication.getName(),
  //          e);
  //      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
  //          .body(ApiResponse.error("Failed to delete word"));
  //    }
  //  }

  private WordSetResponse mapToWordSetResponse(WordSet wordSet) {
    List<WordResponse> words = new ArrayList<>();

    // Map the actual words from the wordSet
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
