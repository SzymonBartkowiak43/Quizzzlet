package com.example.quizlecikprojekt.domain.wordset;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class WordSetTransactionalService {

  private final WordSetRepository wordSetRepository;

  public WordSetTransactionalService(WordSetRepository wordSetRepository) {
    this.wordSetRepository = wordSetRepository;
  }

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
