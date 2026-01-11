package pl.bartkowiak.quizlecikprojekt.domain.wordset;

import pl.bartkowiak.quizlecikprojekt.controllers.dto.word.WordResponse;
import pl.bartkowiak.quizlecikprojekt.controllers.dto.wordset.WordSetResponse;
import pl.bartkowiak.quizlecikprojekt.entity.Word;
import pl.bartkowiak.quizlecikprojekt.entity.WordSet;

import java.util.ArrayList;
import java.util.List;

class WordSetMapper {

  static WordSetResponse mapToWordSetResponse(WordSet wordSet) {
    List<WordResponse> words = new ArrayList<>();

    if (wordSet.getWords() != null) {
      words = wordSet.getWords().stream().map(WordSetMapper::mapToWordResponse).toList();
    }

    return mapToWordSetResponse(wordSet, words);
  }

  static WordSetResponse mapToWordSetResponse(WordSet wordSet, List<WordResponse> wordResponses) {
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

  static WordResponse mapToWordResponse(Word word) {
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
