package com.example.quizlecikprojekt.controllers;

import static org.springframework.http.HttpStatus.CREATED;

import com.example.quizlecikprojekt.controllers.dto.word.WordDeleteRequest;
import com.example.quizlecikprojekt.controllers.dto.word.WordResponse;
import com.example.quizlecikprojekt.controllers.dto.wordset.WordSetCreateRequest;
import com.example.quizlecikprojekt.controllers.dto.wordset.WordSetResponse;
import com.example.quizlecikprojekt.controllers.dto.wordset.WordSetUpdateRequest;
import com.example.quizlecikprojekt.controllers.dto.wordset.WordUpdateRequest;
import com.example.quizlecikprojekt.domain.wordset.dto.WordAddRequest;
import com.example.quizlecikprojekt.domain.wordset.dto.WordAddResponse;
import com.example.quizlecikprojekt.domain.wordset.WordSetFacade;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/word-sets")
@CrossOrigin(origins = "http://68.183.66.208:3000")
public class WordSetRestController {

  private final WordSetFacade wordSetFacade;

  @PostMapping
  public ResponseEntity<WordSetResponse> createWordSet(
      Authentication authentication, @Valid @RequestBody WordSetCreateRequest request) {

    WordSetResponse response = wordSetFacade.createNewWordSet(authentication.getName(), request);

    return ResponseEntity.status(CREATED).body(response);
  }

  @PostMapping("/{wordSetId}/words")
  public ResponseEntity<WordAddResponse> addWordsToWordSet(
      Authentication authentication,
      @PathVariable Long wordSetId,
      @Valid @RequestBody WordAddRequest request) {

    WordAddResponse response =
        wordSetFacade.addWordsToWordSet(authentication.getName(), wordSetId, request);

    return ResponseEntity.status(CREATED).body(response);
  }

  @GetMapping
  public ResponseEntity<List<WordSetResponse>> getWordSets(Authentication authentication) {

    List<WordSetResponse> response = wordSetFacade.getWordSets(authentication.getName());

    return ResponseEntity.ok(response);
  }

  @PutMapping("/{wordSetId}")
  public ResponseEntity<WordSetResponse> updateWordSet(
      Authentication authentication,
      @PathVariable Long wordSetId,
      @Valid @RequestBody WordSetUpdateRequest request) {

    WordSetResponse response =
        wordSetFacade.updateWordSet(authentication.getName(), wordSetId, request);

    return ResponseEntity.ok(response);
  }

  @PutMapping("/{wordSetId}/words/{wordId}")
  public ResponseEntity<WordResponse> updateWord(
      Authentication authentication,
      @PathVariable Long wordSetId,
      @PathVariable Long wordId,
      @Valid @RequestBody WordUpdateRequest request) {

    WordResponse response =
        wordSetFacade.updateWord(authentication.getName(), wordSetId, wordId, request);

    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{wordSetId}")
  public ResponseEntity<?> deleteWordSet(
      Authentication authentication, @PathVariable Long wordSetId) {

    wordSetFacade.deleteWordSet(authentication.getName(), wordSetId);

    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{wordSetId}/words/{wordId}")
  public ResponseEntity<?> deleteWord(
      Authentication authentication, @PathVariable Long wordSetId, @PathVariable Long wordId) {

    wordSetFacade.deleteWord(authentication.getName(), wordSetId, wordId);

    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{wordSetId}/words")
  public ResponseEntity<?> deleteMultipleWords(
      Authentication authentication,
      @PathVariable Long wordSetId,
      @RequestBody WordDeleteRequest request) {

    wordSetFacade.deleteWords(authentication.getName(), wordSetId, request.wordIds());

    return ResponseEntity.ok().build();
  }
}
