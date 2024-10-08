package com.example.quizlecikprojekt.domain.wordSet;


import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserRepository;
import com.example.quizlecikprojekt.domain.word.Word;
import com.example.quizlecikprojekt.domain.word.WordRepository;
import javassist.NotFoundException;
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

    @Transactional
    public void updateWordSet(Long id, WordSet wordSetForm) throws NotFoundException {
        Optional<WordSet> wordSetOptional = getWordSetById(id);
        if (wordSetOptional.isEmpty()) {
            throw new NotFoundException("WordSet not found");
        }

        WordSet wordSet = wordSetOptional.get();
        wordSet.setTitle(wordSetForm.getTitle());
        wordSet.setDescription(wordSetForm.getDescription());
        wordSet.setLanguage(wordSetForm.getLanguage());
        wordSet.setTranslationLanguage(wordSetForm.getTranslationLanguage());

        List<Word> existingWords = wordSet.getWords();
        existingWords.size(); // to initialize lazy loading
        Map<Long, Word> existingWordsMap = existingWords.stream()
                .collect(Collectors.toMap(Word::getId, word -> word));

        for (Word formWord : wordSetForm.getWords()) {
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

        List<Long> formWordIds = wordSetForm.getWords().stream()
                .map(Word::getId)
                .toList();
        wordSet.getWords().removeIf(word -> word.getId() != null && !formWordIds.contains(word.getId()));

        saveWordSet(wordSet);
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
