package com.example.quizlecikprojekt.web;

import com.example.quizlecikprojekt.word.Word;
import com.example.quizlecikprojekt.wordSet.WordSet;
import com.example.quizlecikprojekt.wordSet.WordSetService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Controller
public class ZestawyController {

    private final WordSetService wordSetService;


    public ZestawyController(WordSetService wordSetService) {
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
        model.addAttribute("words", words);
        return "wordSetMenu";
    }

    @GetMapping("/wordSet/{id}/edit")
    public String editWordSet(@PathVariable Long id, Model model) {
        Optional<WordSet> wordSetOptional = wordSetService.getWordSetById(id);

        if (wordSetOptional.isEmpty()) {
            return "redirect:/error";
        }

        WordSet wordSet = wordSetOptional.get();
        model.addAttribute("wordSet", wordSet);
        return "wordSetEdit";
    }

}
