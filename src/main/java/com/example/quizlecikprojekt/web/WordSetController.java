package com.example.quizlecikprojekt.web;

import com.example.quizlecikprojekt.word.Word;
import com.example.quizlecikprojekt.word.WordRepository;
import com.example.quizlecikprojekt.wordSet.WordSet;
import com.example.quizlecikprojekt.wordSet.WordSetService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class WordSetController {

    private final WordSetService wordSetService;
    private final WordRepository wordRepository;


    public WordSetController(WordSetService wordSetService, WordRepository wordRepository) {
        this.wordSetService = wordSetService;
        this.wordRepository = wordRepository;
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

        // Clear existing words to prevent duplication
        wordSet.getWords().clear();

        // Re-add updated words from form
        for (Word formWord : wordSetForm.getWords()) {
            formWord.setWordSet(wordSet); // Set the wordSet for each word
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

        wordRepository.deleteById(wordId);

        return "redirect:/wordSet/" + id + "/edit";
    }


}
