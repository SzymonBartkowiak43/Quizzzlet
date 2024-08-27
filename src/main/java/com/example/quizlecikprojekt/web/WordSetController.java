package com.example.quizlecikprojekt.web;


import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserService;
import com.example.quizlecikprojekt.domain.word.Word;
import com.example.quizlecikprojekt.domain.word.WordService;
import com.example.quizlecikprojekt.domain.wordSet.WordSet;
import com.example.quizlecikprojekt.domain.wordSet.WordSetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class WordSetController {
    private final WordSetService wordSetService;
    private final WordService wordService;
    private final UserService userService;
    private final static Logger LOGGER = LoggerFactory.getLogger(WordSetController.class);


    public WordSetController(WordSetService wordSetService, WordService wordService, UserService userService) {
        this.wordSetService = wordSetService;
        this.wordService = wordService;
        this.userService = userService;
    }

    @GetMapping("/wordSet")
    public String getWordSets(Model model, Authentication authentication) {
        LOGGER.info("Entering getWordSets method");
        String email = authentication.getName();
        List<WordSet> wordSets = wordSetService.getWordSetsByEmail(email);
        model.addAttribute("wordSets", wordSets);
        LOGGER.info("Returning wordSet view");
        return "wordSet";
    }

    @GetMapping("/wordSet/create")
    public String createWordSet() {
        LOGGER.info("Entering createWordSet method");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.getUserByEmail(userDetails.getUsername());

        WordSet wordSet = new WordSet();
        wordSet.setUser(user);
        wordSet.setTitle("New Word Set");
        wordSet.setDescription("Description");
        wordSet.setLanguage("pl");
        wordSet.setTranslationLanguage("en");

        wordSetService.createWordSet(wordSet);
        LOGGER.info("WordSet created and redirecting to /wordSet");
        return "redirect:/wordSet";
    }

    @GetMapping("/wordSet/{id}")
    public String showWords(@PathVariable Long id, Model model) {
        LOGGER.info("Entering showWords method with id: {}", id);
        List<Word> words = wordSetService.getWordsByWordSetId(id);
        Optional<WordSet> wordSetOptional = wordSetService.getWordSetById(id);

        if (wordSetOptional.isEmpty()) {
            LOGGER.warn("WordSet not found for id: {}", id);
            return "redirect:/error";
        }

        WordSet wordSet = wordSetOptional.get();
        model.addAttribute("wordSet", wordSet);
        model.addAttribute("words", words);
        LOGGER.info("Returning wordSetMenu view for id: {}", id);
        return "wordSetMenu";
    }

    @GetMapping("/wordSet/{id}/edit")
    public String editWordSet(@PathVariable Long id, Model model) {
        Optional<WordSet> wordSetOptional = wordSetService.getWordSetById(id);
        List<Word> words = wordSetService.getWordsByWordSetId(id);

        if (wordSetOptional.isEmpty()) {
            return "redirect:/error";
        }

        WordSet wordSet = wordSetOptional.get();

        model.addAttribute("wordSet", wordSet);
        model.addAttribute("words", words);
        return "wordSetEdit";
    }

    @PostMapping("/wordSet/{id}/update")
    public String updateWordSet(@PathVariable Long id, @ModelAttribute WordSet wordSetForm, BindingResult result) {
        if (result.hasErrors()) {
            return "redirect:/error";
        }

        Optional<WordSet> wordSetOptional = wordSetService.getWordSetById(id);
        if (wordSetOptional.isEmpty()) {
            return "redirect:/error";
        }

        WordSet wordSet = wordSetOptional.get();
        wordSet.setTitle(wordSetForm.getTitle());
        wordSet.setDescription(wordSetForm.getDescription());
        wordSet.setLanguage(wordSetForm.getLanguage());
        wordSet.setTranslationLanguage(wordSetForm.getTranslationLanguage());


        List<Word> existingWords = wordSet.getWords();
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


        wordSetService.saveWordSet(wordSet);

        return "redirect:/wordSet/" + id + "/edit";
    }

    private boolean checkExisiting(Word word, List<Word> words) {
        for (Word w : words) {
          if(w.getWord().equals(word.getWord()) && w.getTranslation().equals(word.getTranslation())) {
            return true;
          }
        }
        return false;
    }

    @GetMapping("/wordSet/{id}/deleteWord/{wordId}")
    public String deleteWordFromWordSet(@PathVariable Long id, @PathVariable Long wordId) {

        Optional<WordSet> wordSetOptional = wordSetService.getWordSetById(id);
        if (wordSetOptional.isEmpty()) {
            return "redirect:/error";
        }

        wordService.deleteWordById(wordId);

        return "redirect:/wordSet/" + id + "/edit";
    }

    @PostMapping("/wordSet/delete")
    public String deleteWordSet(@RequestParam Long wordSetIdToDelete) {
        wordSetService.deleteWordSet(wordSetIdToDelete);
        return "redirect:/wordSet";
    }




}
