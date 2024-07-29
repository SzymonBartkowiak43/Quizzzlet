package com.example.quizlecikprojekt.web;

import com.example.quizlecikprojekt.word.Dto.WordToRepeadDto;
import com.example.quizlecikprojekt.word.WordService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;

@Controller
public class HomeController {
    private final WordService wordService;
    private int correctWordOnView = 0;
    private List<WordToRepeadDto> wordsToRepeat;
    private boolean systemAddCorrectWord;

    public HomeController(WordService wordService) {
        this.wordService = wordService;
    }


    @GetMapping("/")
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isLoggedIn = authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser");
        model.addAttribute("isLoggedIn", isLoggedIn);

        wordsToRepeat = wordService.getWordsToRepeat();
        Collections.shuffle(wordsToRepeat);
        correctWordOnView = 0;
        for(int i = 0; i < 8; i++) {
            WordToRepeadDto wordToRepeadDto = wordsToRepeat.get(i);
            if(wordToRepeadDto.isCorrect()) {
                correctWordOnView++;
            }
        }

        System.out.println("numberOfCorrectWords: " + correctWordOnView);


        model.addAttribute("wordsToRepeat", wordsToRepeat);
        return "home";
    }

    @GetMapping("/reloadFlashcard")
    @ResponseBody
    public WordToRepeadDto reloadFlashcard() {
        Collections.shuffle(wordsToRepeat);
        if(wordsToRepeat.get(0).isCorrect()) {
            systemAddCorrectWord = true;
        } else {
           systemAddCorrectWord = false;
        }

        return wordsToRepeat.get(0);
    }

    @GetMapping("/checkAnswer/{word}/{translation}")
    @ResponseBody
    public int checkAnswer(@PathVariable String word, @PathVariable String translation) {
        WordToRepeadDto wordToRepeadDto = wordsToRepeat.stream()
                .filter(w -> w.getWord().equals(word) && w.getTranslation().equals(translation))
                .findFirst()
                .orElse(null);
        if (wordToRepeadDto != null && !wordToRepeadDto.isCorrect()) {
            if(systemAddCorrectWord) {
                correctWordOnView++;
            }
        } else {
            if(!systemAddCorrectWord) {
                correctWordOnView--;
            }
        }
        System.out.println("numberOfCorrectWords: " + correctWordOnView);
        return correctWordOnView;
    }

}
