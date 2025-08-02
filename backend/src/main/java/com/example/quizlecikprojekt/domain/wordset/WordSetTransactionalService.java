package com.example.quizlecikprojekt.domain.wordset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WordSetTransactionalService {

  private static final Logger logger = LoggerFactory.getLogger(WordSetTransactionalService.class);

  private final WordSetRepository wordSetRepository;

  public WordSetTransactionalService(WordSetRepository wordSetRepository) {
    this.wordSetRepository = wordSetRepository;
  }

  @Transactional
  public WordSet saveWordSet(WordSet wordSet) {
    WordSet savedWordSet = wordSetRepository.save(wordSet);
    logger.info("WordSet saved successfully with id: {}", savedWordSet.getId());
    return savedWordSet;
  }

  @Transactional
  public WordSet createWordSet(WordSet wordSet) {
    WordSet createdWordSet = wordSetRepository.save(wordSet);
    logger.info(
        "WordSet created successfully for user: {} with id: {}",
        wordSet.getUser().getEmail(),
        createdWordSet.getId());
    return createdWordSet;
  }

  @Transactional
  public void deleteWordSet(Long id) {
    wordSetRepository.deleteById(id);
    logger.info("WordSet deleted successfully with id: {}", id);
  }

  @Transactional
  public WordSet updateWordSet(WordSet wordSet) {
    WordSet updatedWordSet = wordSetRepository.save(wordSet);
    logger.info("WordSet updated successfully with id: {}", updatedWordSet.getId());
    return updatedWordSet;
  }
}
