package com.example.quizlecikprojekt.domain.wordset;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
class WordSetTransactionalService {

  private final WordSetRepository wordSetRepository;

  @Transactional
  public WordSet createWordSet(WordSet wordSet) {
    return wordSetRepository.save(wordSet);
  }

  @Transactional
  public void deleteWordSet(Long id) {
    wordSetRepository.deleteById(id);
  }

  @Transactional
  public WordSet updateWordSet(WordSet wordSet) {
    return wordSetRepository.save(wordSet);
  }
}
