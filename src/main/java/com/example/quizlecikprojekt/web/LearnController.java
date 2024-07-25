package com.example.quizlecikprojekt.web;

import com.example.quizlecikprojekt.word.Word;
import com.example.quizlecikprojekt.wordSet.WordSetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;
import java.util.List;

@Controller
public class LearnController {

    private final WordSetService wordSetService;

    public LearnController(WordSetService wordSetService) {
        this.wordSetService = wordSetService;
    }

    @GetMapping("/wordSet/{id}/test")
    public String learnTest(@PathVariable long id, Model model) {
        List<Word> words = wordSetService.getWordsByWordSetId(id);
        if(words.size() < 4) {
            model.addAttribute("error", "Word set must have at least 4 words to start learning");
            return "error";
        } else {
            Collections.shuffle(words);
            model.addAttribute("words", words);
            return "learnTest";
        }
    }

    @PostMapping("/wordSet/{id}/submitTest")
    public String submitTest(@PathVariable long id, Model model) {
        List<Word> correctWords = wordSetService.getWordsByWordSetId(id);
        int score = 0;
        //sout all user answers


        for (Word word : correctWords) {
            String correctAnswer = word.getTranslation();

           // String userAnswer = submission.getAnswers().get(word.getId());

//            if (correctAnswer.equalsIgnoreCase(userAnswer)) {
//                score++;
//            }
        }

        model.addAttribute("score", score);
        return "testResult";
    }


}
