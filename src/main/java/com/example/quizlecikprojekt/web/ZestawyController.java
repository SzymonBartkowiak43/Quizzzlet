package com.example.quizlecikprojekt.web;

import com.example.quizlecikprojekt.wordSet.WordSet;
import com.example.quizlecikprojekt.wordSet.WordSetService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ZestawyController {

    private final WordSetService wordSetService;


    public ZestawyController(WordSetService wordSetService) {
        this.wordSetService = wordSetService;
    }
    @GetMapping("/zestawy")
    public String getWordSets(Model model, Authentication authentication) {
        String email = authentication.getName(); //email
        List<WordSet> wordSets = wordSetService.getWordSetsByEmail(email);
        model.addAttribute("wordSets", wordSets);
        return "zestawy";
    }
}
