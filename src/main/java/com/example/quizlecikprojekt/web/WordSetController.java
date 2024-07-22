package com.example.quizlecikprojekt.web;

import com.example.quizlecikprojekt.word.Word;
import com.example.quizlecikprojekt.wordSet.WordSet;
import com.example.quizlecikprojekt.wordSet.WordSetService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class WordSetController {

    private final WordSetService wordSetService;


    public WordSetController(WordSetService wordSetService) {
        this.wordSetService = wordSetService;
    }

    @GetMapping("/wordSet")
    public String getWordSets(Model model, Authentication authentication) {
        String email = authentication.getName(); //email
        List<WordSet> wordSets = wordSetService.getWordSetsByEmail(email);
        model.addAttribute("wordSets", wordSets);
        return "wordSet";
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

    @GetMapping("/wordSet/{id}/learn")
    public String learnWordSet(@PathVariable("id") Long id, Model model) {
        return "wordSetLearn";
    }

    @PostMapping("/wordSet/{id}/update")
    public String updateWordSet(@PathVariable Long id, @ModelAttribute WordSet wordSet, Model model) {
        Optional<WordSet> existingWordSet = wordSetService.getWordSetById(id);
        if (existingWordSet.isEmpty()) {
            return "redirect:/error";
        }

        wordSet.setId(id);
        wordSetService.saveWordSet(wordSet);

        return "redirect:/wordSet";
    }

    @PostMapping("/wordSet/{wordSetId}/deleteWord")
    public String deleteWord(@PathVariable Long wordSetId, @RequestParam("id") Long wordId) {
        wordSetService.deleteWordById(wordId);
        return "redirect:/wordSet/" + wordSetId;
    }

}
