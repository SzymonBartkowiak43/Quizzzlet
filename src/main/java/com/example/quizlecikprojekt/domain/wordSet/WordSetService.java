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
    private final WordSetRepository wordSetRepository;
    private final UserRepository userRepository;
    private final WordRepository wordRepository;

    private final static Logger LOGGER = LoggerFactory.getLogger(WordSetService.class);

    public WordSetService(WordSetRepository wordSetRepository, UserRepository userRepository, WordRepository wordRepository) {
        this.wordSetRepository = wordSetRepository;
        this.userRepository = userRepository;
        this.wordRepository = wordRepository;
    }

    public List<WordSet> getWordSetsByEmail(String email) {
        LOGGER.info("Entering getWordSetsByEmail with email: {}", email);
        Optional<User> user = userRepository.findByEmail(email);
        List<WordSet> wordSets = wordSetRepository.findByUser(user);
        LOGGER.info("Returning word sets for email: {}", email);
        return wordSets;
    }

    public List<Word> getWordsByWordSetId(Long wordSetId) {
        LOGGER.info("Entering getWordsByWordSetId with wordSetId: {}", wordSetId);
        Optional<WordSet> wordSet = wordSetRepository.findById(wordSetId);
        List<Word> words = wordRepository.findByWordSet(wordSet);
        LOGGER.info("Returning words for wordSetId: {}", wordSetId);
        return words;
    }

    public Optional<WordSet> getWordSetById(Long wordSetId) {
        LOGGER.info("Entering getWordSetById with wordSetId: {}", wordSetId);
        Optional<WordSet> wordSet = wordSetRepository.findById(wordSetId);
        LOGGER.info("Returning word set for wordSetId: {}", wordSetId);
        return wordSet;
    }

    @Transactional
    public void saveWordSet(WordSet wordSet) {
        LOGGER.info("Entering saveWordSet with wordSet: {}", wordSet);
        wordSetRepository.save(wordSet);
        LOGGER.info("Word set saved successfully");
    }

    @Transactional
    public void createWordSet(WordSet wordSet) {
        LOGGER.info("Entering createWordSet with wordSet: {}", wordSet);
        wordSetRepository.save(wordSet);
        LOGGER.info("Word set created successfully");
    }

    @Transactional
    public void deleteWordSet(Long id) {
        LOGGER.info("Entering deleteWordSet with id: {}", id);
        wordSetRepository.deleteById(id);
        LOGGER.info("Word set deleted successfully");
    }

    public void updateWordSetDetails(WordSet wordSet, WordSet wordSetForm) {
        wordSet.setTitle(wordSetForm.getTitle());
        wordSet.setDescription(wordSetForm.getDescription());
        wordSet.setLanguage(wordSetForm.getLanguage());
        wordSet.setTranslationLanguage(wordSetForm.getTranslationLanguage());
    }

    public void updateWordsInWordSet(WordSet updatedWordSet, WordSet existingWordSet) {
        Map<Long, Word> existingWordsMap = mapExistingWords(existingWordSet);

        updateOrAddWords(updatedWordSet, existingWordSet, existingWordsMap);

        removeDeletedWords(updatedWordSet, existingWordSet);
    }

    public  Map<Long, Word> mapExistingWords(WordSet wordSet) {
        return wordSet.getWords().stream()
                .collect(Collectors.toMap(Word::getId, word -> word));
    }

    public void updateOrAddWords(WordSet updatedWordSet, WordSet existingWordSet, Map<Long, Word> existingWordsMap) {
        for (Word updatedWord : updatedWordSet.getWords()) {
            if (updatedWord.getId() != null && existingWordsMap.containsKey(updatedWord.getId())) {
                Word existingWord = existingWordsMap.get(updatedWord.getId());
                if (!existingWord.getWord().equals(updatedWord.getWord()) ||
                        !existingWord.getTranslation().equals(updatedWord.getTranslation())) {
                    existingWord.setWord(updatedWord.getWord());
                    existingWord.setTranslation(updatedWord.getTranslation());
                }
            } else {
                updatedWord.setWordSet(existingWordSet);
                updatedWord.setPoints(0);
                updatedWord.setLastPracticed(Date.valueOf(LocalDateTime.now().toLocalDate()));
                existingWordSet.getWords().add(updatedWord);
            }
        }
    }

    public void removeDeletedWords(WordSet updatedWordSet, WordSet existingWordSet) {
        List<Long> updatedWordIds = updatedWordSet.getWords().stream()
                .map(Word::getId)
                .toList();
        existingWordSet.getWords().removeIf(word -> word.getId() != null && !updatedWordIds.contains(word.getId()));
    }

    public WordSet newWordSet(User user) {
        WordSet wordSet = new WordSet();
        wordSet.setUser(user);
        wordSet.setTitle("New Word Set");
        wordSet.setDescription("Description");
        wordSet.setLanguage("pl");
        wordSet.setTranslationLanguage("en");
        return wordSet;
    }

}
