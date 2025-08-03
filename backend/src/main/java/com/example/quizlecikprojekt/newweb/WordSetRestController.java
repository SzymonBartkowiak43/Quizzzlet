package com.example.quizlecikprojekt.newweb;

import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserService;
import com.example.quizlecikprojekt.domain.user.dto.UserDto;
import com.example.quizlecikprojekt.domain.word.Word;
import com.example.quizlecikprojekt.domain.word.WordService;
import com.example.quizlecikprojekt.domain.wordset.WordSet;
import com.example.quizlecikprojekt.domain.wordset.WordSetService;
import com.example.quizlecikprojekt.newweb.dto.ApiResponse;
import com.example.quizlecikprojekt.newweb.dto.wordset.WordSetCreateRequest;
import com.example.quizlecikprojekt.newweb.dto.wordset.WordSetResponse;
import com.example.quizlecikprojekt.newweb.dto.wordset.WordSetUpdateRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wordsets")
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

  @GetMapping
  public ResponseEntity<ApiResponse<List<WordSetResponse>>> getWordSets(
      Authentication authentication) {
    try {
      if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("User not authenticated"));
      }

      String email = authentication.getName();
      List<WordSet> wordSets = wordSetService.getWordSetsByEmail(email);
      List<WordSetResponse> response = wordSets.stream().map(this::mapToWordSetResponse).toList();

      return ResponseEntity.ok(ApiResponse.success("Word sets retrieved successfully", response));

    } catch (Exception e) {
      logger.error("Error retrieving word sets for user: {}", authentication.getName(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Failed to retrieve word sets"));
    }
  }

  @PostMapping
  public ResponseEntity<ApiResponse<WordSetResponse>> createWordSet(
      Authentication authentication, @Valid @RequestBody WordSetCreateRequest request) {
    try {
      if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("User not authenticated"));
      }

      UserDto user = userService.findByEmail(authentication.getName());
      WordSet wordSet = wordSetService.newWordSet(null);

      if (request.name() != null && !request.name().trim().isEmpty()) {
        wordSet.setTitle(request.name());
      }
      if (request.description() != null) {
        wordSet.setDescription(request.description());
      }

      WordSet createdWordSet = wordSetService.createWordSet(wordSet);
      WordSetResponse response = mapToWordSetResponse(createdWordSet);

      logger.info("Word set created successfully for user: {}", authentication.getName());
      return ResponseEntity.status(HttpStatus.CREATED)
          .body(ApiResponse.success("Word set created successfully", response));

    } catch (Exception e) {
      logger.error("Error creating word set for user: {}", authentication.getName(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Failed to create word set"));
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<String>> deleteWordSet(
      @PathVariable Long id, Authentication authentication) {
    try {
      if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("User not authenticated"));
      }

      Optional<WordSet> wordSetOptional = wordSetService.getWordSetById(id);
      if (wordSetOptional.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error("Word set not found"));
      }

      String userEmail = authentication.getName();

      if (wordSetService.isWordSetOwnedByUser(id, userEmail)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Access denied"));
      }

      wordSetService.deleteWordSet(id);

      logger.info("Word set {} deleted by user: {}", id, userEmail);
      return ResponseEntity.ok(ApiResponse.success("Word set deleted successfully", "Deleted"));

    } catch (Exception e) {
      logger.error("Error deleting word set {} for user: {}", id, authentication.getName(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Failed to delete word set"));
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<WordSetResponse>> getWordSetWithWords(
      @PathVariable Long id, Authentication authentication) {
    try {
      if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("User not authenticated"));
      }

      Optional<WordSet> wordSetOptional = wordSetService.getWordSetById(id);
      if (wordSetOptional.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error("Word set not found"));
      }

      WordSet wordSet = wordSetOptional.get();
      String userEmail = authentication.getName();

      if (wordSetService.isWordSetOwnedByUser(id, userEmail)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Access denied"));
      }

      List<Word> words = wordSetService.getWordsByWordSetId(id);
      WordSetResponse response = mapToWordSetResponse(wordSet, words);

      return ResponseEntity.ok(ApiResponse.success("Word set retrieved successfully", response));

    } catch (Exception e) {
      logger.error("Error retrieving word set {} for user: {}", id, authentication.getName(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Failed to retrieve word set"));
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<WordSetResponse>> updateWordSet(
      @PathVariable Long id,
      @Valid @RequestBody WordSetUpdateRequest request,
      Authentication authentication) {
    try {
      if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("User not authenticated"));
      }

      Optional<WordSet> wordSetOptional = wordSetService.getWordSetById(id);
      if (wordSetOptional.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error("Word set not found"));
      }

      String userEmail = authentication.getName();
      if (wordSetService.isWordSetOwnedByUser(id, userEmail)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Access denied"));
      }

      WordSet wordSetForm = new WordSet();
      wordSetForm.setTitle(request.name());
      wordSetForm.setDescription(request.description());

      WordSet updatedWordSet = wordSetService.updateWordSet(id, wordSetForm);
      WordSetResponse response = mapToWordSetResponse(updatedWordSet);

      logger.info("Word set {} updated by user: {}", id, userEmail);
      return ResponseEntity.ok(ApiResponse.success("Word set updated successfully", response));

    } catch (Exception e) {
      logger.error("Error updating word set {} for user: {}", id, authentication.getName(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Failed to update word set"));
    }
  }

  @DeleteMapping("/{id}/words/{wordId}")
  public ResponseEntity<ApiResponse<String>> deleteWordFromWordSet(
      @PathVariable Long id, @PathVariable Long wordId, Authentication authentication) {
    try {
      if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("User not authenticated"));
      }

      Optional<WordSet> wordSetOptional = wordSetService.getWordSetById(id);
      if (wordSetOptional.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error("Word set not found"));
      }

      String userEmail = authentication.getName();
      if (wordSetService.isWordSetOwnedByUser(id, userEmail)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Access denied"));
      }

      wordService.deleteWordById(wordId);

      logger.info("Word {} deleted from word set {} by user: {}", wordId, id, userEmail);
      return ResponseEntity.ok(ApiResponse.success("Word deleted successfully", "Deleted"));

    } catch (Exception e) {
      logger.error(
          "Error deleting word {} from word set {} for user: {}",
          wordId,
          id,
          authentication.getName(),
          e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("Failed to delete word"));
    }
  }

  private WordSetResponse mapToWordSetResponse(WordSet wordSet) {
    return mapToWordSetResponse(wordSet, null);
  }

  private WordSetResponse mapToWordSetResponse(WordSet wordSet, List<Word> words) {
    return WordSetResponse.builder()
        .id(wordSet.getId())
        .title(wordSet.getTitle())
        .description(wordSet.getDescription())
        .language(wordSet.getLanguage())
        .translationLanguage(wordSet.getTranslationLanguage())
        .createdAt(wordSet.getCreatedAt())
        .updatedAt(wordSet.getUpdatedAt())
        .words(words)
        .wordCount(wordSetService.getWordCountByWordSetId(wordSet.getId()))
        .build();
  }
}
