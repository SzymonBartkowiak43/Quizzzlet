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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequestMapping("/wordSet")
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

    @GetMapping("")
    public String getWordSets(Model model, Authentication authentication) {
        LOGGER.info("Entering getWordSets method");
        String email = authentication.getName();
        List<WordSet> wordSets = wordSetService.getWordSetsByEmail(email);
        model.addAttribute("wordSets", wordSets);
        LOGGER.info("Returning wordSet view");
        return "wordSet";
    }

    @PostMapping("")
    public String createWordSet(Authentication authentication) {
        LOGGER.info("Entering createWordSet method");
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.getUserByEmail(userDetails.getUsername());

        if(user == null) {
            LOGGER.error("User not found for email: {}", userDetails.getUsername());
            return "redirect:/error";
        }

        WordSet wordSet = wordSetService.newWordSet(user);

        wordSetService.createWordSet(wordSet);
        LOGGER.info("WordSet created and redirecting to /wordSet");
        return "redirect:/wordSet";
    }


    @PostMapping("/delete")
    public String deleteWordSet(@RequestParam Long wordSetIdToDelete) {
        wordSetService.deleteWordSet(wordSetIdToDelete);
        return "redirect:/wordSet";
    }

    @GetMapping("/{id}")
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

    @GetMapping("/{id}/edit")
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

    @PostMapping("/{id}/update")
    public String updateWordSet(@PathVariable Long id, @ModelAttribute WordSet wordSetForm, BindingResult result) {
        if (result.hasErrors()) {
            return "redirect:/error";
        }

        WordSet wordSet = wordSetService.getWordSetById(id)
                .orElseThrow(() -> new NoSuchElementException("WordSet not found for id: " + id));


        wordSetService.updateWordSetDetails(wordSet, wordSetForm);


        wordSetService.updateWordsInWordSet(wordSetForm, wordSet);


        wordSetService.saveWordSet(wordSet);

        return "redirect:/wordSet/" + id + "/edit";
    }

    @PostMapping("/{id}/deleteWord/{wordId}")
    public String deleteWordFromWordSet(@PathVariable Long id, @PathVariable Long wordId) {
        System.out.println("WTFFFFFFFFFFFFFFFFFF");
        Optional<WordSet> wordSetOptional = wordSetService.getWordSetById(id);
        if (wordSetOptional.isEmpty()) {
            return "redirect:/error";
        }

        wordService.deleteWordById(wordId);

        return "redirect:/wordSet" +
                "/" + id + "/edit";
    }
}
