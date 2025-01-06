package com.example.quizlecikprojekt.web;

import com.example.quizlecikprojekt.domain.user.UserService;
import com.example.quizlecikprojekt.domain.word.dto.WordToRepeadDto;
import com.example.quizlecikprojekt.domain.word.WordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;

@Controller
public class ReviewController {
    private final WordService wordService;
    private final UserService userService;
    private final static Logger LOGGER = LoggerFactory.getLogger(ReviewController.class);

    private int correctWordOnView = 0;
    private List<WordToRepeadDto> wordsToRepeat;
    private boolean systemAddCorrectWord;

    public ReviewController(WordService wordService, UserService userService) {
        this.wordService = wordService;
        this.userService = userService;
    }

    @GetMapping("/review")
    public String home(Model model) {
        LOGGER.info("Entering review method");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        Long userId = userService.getUserIdByUsername(username);

        wordsToRepeat = wordService.getWordsToRepeat(userId);
        Collections.shuffle(wordsToRepeat);
        correctWordOnView = 0;
        for(int i = 0; i < 8; i++) {
            WordToRepeadDto wordToRepeadDto = wordsToRepeat.get(i);
            if(wordToRepeadDto.isCorrect()) {
                correctWordOnView++;
            }
        }

        LOGGER.info("Number of correct words on view: {}", correctWordOnView);

        model.addAttribute("wordsToRepeat", wordsToRepeat);
        return "review";
    }

    @GetMapping("/reloadFlashcard")
    @ResponseBody
    public WordToRepeadDto reloadFlashcard() {
        LOGGER.info("Entering reloadFlashcard method");

        Collections.shuffle(wordsToRepeat);
        systemAddCorrectWord = wordsToRepeat.get(0).isCorrect();

        return wordsToRepeat.get(0);
    }

    @GetMapping("/checkAnswer/{word}/{translation}")
    @ResponseBody
    public int checkAnswer(@PathVariable String word, @PathVariable String translation) {
        LOGGER.info("Entering checkAnswer method with word: {} and translation: {}", word, translation);
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
        LOGGER.info("Number of correct words on view after check: {}", correctWordOnView);
        return correctWordOnView;
    }

}
