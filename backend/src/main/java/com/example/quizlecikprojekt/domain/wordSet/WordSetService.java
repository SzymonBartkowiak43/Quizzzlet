package com.example.quizlecikprojekt.domain.wordSet;

import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserRepository;
import com.example.quizlecikprojekt.domain.word.Word;
import com.example.quizlecikprojekt.domain.word.WordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WordSetService {

    private static final Logger logger = LoggerFactory.getLogger(WordSetService.class);

    private final WordSetRepository wordSetRepository;
    private final UserRepository userRepository;
    private final WordRepository wordRepository;

    public WordSetService(WordSetRepository wordSetRepository, UserRepository userRepository, WordRepository wordRepository) {
        this.wordSetRepository = wordSetRepository;
        this.userRepository = userRepository;
        this.wordRepository = wordRepository;
    }

    public List<WordSet> getWordSetsByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            logger.warn("User not found with email: {}", email);
            return List.of();
        }

        User user = userOptional.get();
        return wordSetRepository.findByUser(Optional.of(user));
    }

    public List<Word> getWordsByWordSetId(Long wordSetId) {
        Optional<WordSet> wordSetOptional = wordSetRepository.findById(wordSetId);
        if (wordSetOptional.isEmpty()) {
            logger.warn("WordSet not found with id: {}", wordSetId);
            return List.of();
        }

        WordSet wordSet = wordSetOptional.get();
        return wordRepository.findByWordSet(Optional.of(wordSet));
    }

    public Optional<WordSet> getWordSetById(Long wordSetId) {
        return wordSetRepository.findById(wordSetId);
    }

    public boolean isWordSetOwnedByUser(Long wordSetId, String userEmail) {
        Optional<WordSet> wordSetOptional = getWordSetById(wordSetId);
        if (wordSetOptional.isEmpty()) {
            return false;
        }

        WordSet wordSet = wordSetOptional.get();
        return wordSet.getUser().getEmail().equals(userEmail);
    }

    public int getWordCountByWordSetId(Long wordSetId) {
        return getWordsByWordSetId(wordSetId).size();
    }

    @Transactional
    public WordSet saveWordSet(WordSet wordSet) {
        try {
            WordSet savedWordSet = wordSetRepository.save(wordSet);
            logger.info("WordSet saved successfully with id: {}", savedWordSet.getId());
            return savedWordSet;
        } catch (Exception e) {
            logger.error("Error saving WordSet: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save WordSet", e);
        }
    }

    @Transactional
    public WordSet createWordSet(WordSet wordSet) {
        try {
            // @PrePersist automatycznie ustawi timestamps
            WordSet createdWordSet = wordSetRepository.save(wordSet);
            logger.info("WordSet created successfully for user: {} with id: {}",
                    wordSet.getUser().getEmail(), createdWordSet.getId());
            return createdWordSet;
        } catch (Exception e) {
            logger.error("Error creating WordSet for user: {}", wordSet.getUser().getEmail(), e);
            throw new RuntimeException("Failed to create WordSet", e);
        }
    }

    @Transactional
    public void deleteWordSet(Long id) {
        try {
            Optional<WordSet> wordSetOptional = getWordSetById(id);
            if (wordSetOptional.isEmpty()) {
                throw new RuntimeException("WordSet not found with id: " + id);
            }

            WordSet wordSet = wordSetOptional.get();
            logger.info("Deleting WordSet with id: {} for user: {}", id, wordSet.getUser().getEmail());

            wordSetRepository.deleteById(id);
            logger.info("WordSet deleted successfully with id: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting WordSet with id: {}", id, e);
            throw new RuntimeException("Failed to delete WordSet", e);
        }
    }

    @Transactional
    public WordSet updateWordSet(Long id, WordSet wordSetForm) {
        try {
            Optional<WordSet> wordSetOptional = getWordSetById(id);
            if (wordSetOptional.isEmpty()) {
                throw new RuntimeException("WordSet not found with id: " + id);
            }

            WordSet existingWordSet = wordSetOptional.get();

            // Aktualizuj podstawowe pola
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

            // @PreUpdate automatycznie ustawi updatedAt

            // Jeśli mamy słowa do zaktualizowania
            if (wordSetForm.getWords() != null && !wordSetForm.getWords().isEmpty()) {
                updateWords(existingWordSet, wordSetForm.getWords());
            }

            WordSet updatedWordSet = saveWordSet(existingWordSet);
            logger.info("WordSet updated successfully with id: {}", id);
            return updatedWordSet;

        } catch (Exception e) {
            logger.error("Error updating WordSet with id: {}", id, e);
            throw new RuntimeException("Failed to update WordSet", e);
        }
    }

    private void updateWords(WordSet wordSet, List<Word> newWords) {
        List<Word> existingWords = wordSet.getWords();
        existingWords.size(); // Initialize lazy loading

        Map<Long, Word> existingWordsMap = existingWords.stream()
                .collect(Collectors.toMap(Word::getId, word -> word));

        for (Word formWord : newWords) {
            if (formWord.getId() != null && existingWordsMap.containsKey(formWord.getId())) {
                Word existingWord = existingWordsMap.get(formWord.getId());
                if (!existingWord.getWord().equals(formWord.getWord()) ||
                        !existingWord.getTranslation().equals(formWord.getTranslation())) {
                    existingWord.setWord(formWord.getWord());
                    existingWord.setTranslation(formWord.getTranslation());
                }
            } else {
                formWord.setWordSet(wordSet);
                formWord.setPoints(0);
                formWord.setLastPracticed(Date.valueOf(LocalDateTime.now().toLocalDate()));
                wordSet.getWords().add(formWord);
            }
        }

        List<Long> formWordIds = newWords.stream()
                .map(Word::getId)
                .toList();
        wordSet.getWords().removeIf(word -> word.getId() != null && !formWordIds.contains(word.getId()));
    }

    public WordSet newWordSet(User user) {
        WordSet wordSet = new WordSet();
        wordSet.setUser(user);
        wordSet.setTitle("New Word Set");
        wordSet.setDescription("Description");
        wordSet.setLanguage("pl");
        wordSet.setTranslationLanguage("en");
        // createdAt i updatedAt będą ustawione przez @PrePersist

        logger.debug("Created new WordSet template for user: {}", user.getEmail());
        return wordSet;
    }

    public boolean wordSetExists(Long wordSetId) {
        return wordSetRepository.existsById(wordSetId);
    }
}