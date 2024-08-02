package com.example.quizlecikprojekt.web;

import com.example.quizlecikprojekt.user.User;
import com.example.quizlecikprojekt.user.UserService;
import com.example.quizlecikprojekt.word.Word;
import com.example.quizlecikprojekt.word.WordService;
import com.example.quizlecikprojekt.wordSet.WordSet;
import com.example.quizlecikprojekt.wordSet.WordSetService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class WordSetController {
    private final WordSetService wordSetService;
    private final WordService wordService;
    private final UserService userService;


    public WordSetController(WordSetService wordSetService, WordService wordService, UserService userService) {
        this.wordSetService = wordSetService;
        this.wordService = wordService;
        this.userService = userService;
    }

    @GetMapping("/wordSet")
    public String getWordSets(Model model, Authentication authentication) {
        String email = authentication.getName(); //email
        List<WordSet> wordSets = wordSetService.getWordSetsByEmail(email);
        model.addAttribute("wordSets", wordSets);
        return "wordSet";
    }

    @GetMapping("/wordSet/create")
    public String createWordSet() {
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
        return "redirect:/wordSet";
    }

    @GetMapping("/wordSet/{id}")
    public String showWords(@PathVariable Long id, Model model) {
        List<Word> words = wordSetService.getWordsByWordSetId(id);
        Optional<WordSet> wordSetOptional = wordSetService.getWordSetById(id);

        if (wordSetOptional.isEmpty()) {
            return "redirect:/error";
        }

        WordSet wordSet = wordSetOptional.get();
        model.addAttribute("wordSet", wordSet);
        model.addAttribute("words", words);
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
    public String updateWordSet(@PathVariable Long id, @ModelAttribute WordSet wordSetForm, BindingResult result, Model model) {
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

        wordSet.getWords().clear();

        for (Word formWord : wordSetForm.getWords()) {
            formWord.setWordSet(wordSet);
            wordSet.getWords().add(formWord);
        }

        wordSetService.saveWordSet(wordSet);

        return "redirect:/wordSet/" + id + "/edit";
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
