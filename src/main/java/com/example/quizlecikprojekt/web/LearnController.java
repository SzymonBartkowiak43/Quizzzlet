package com.example.quizlecikprojekt.web;

import com.example.quizlecikprojekt.domain.word.Word;
import com.example.quizlecikprojekt.domain.word.WordService;
import com.example.quizlecikprojekt.domain.wordSet.WordSetService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.*;

@Controller
public class LearnController {

    private final WordSetService wordSetService;
    private final WordService wordService;

    private int nextWordIndex = 0;
    private List<Word> words;
    private int score = 0;
    private final List<Word> uncorrectedWords = new ArrayList<>();
    private final static Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    public LearnController(WordSetService wordSetService, WordService wordService) {
        this.wordSetService = wordSetService;
        this.wordService = wordService;
    }

    @GetMapping("/wordSet/{id}/flashCards")
    public String leranFlashCards(@PathVariable long id, Model model) {
        LOGGER.info("Entering leranFlashCards with wordSetId: {}", id);
        if(nextWordIndex == 0) {
            uncorrectedWords.clear();
            words = wordSetService.getWordsByWordSetId(id);
            score = 0;
            Collections.shuffle(words);
        }

        if(nextWordIndex >= words.size()) {
            words.clear();
            nextWordIndex = 0;
            model.addAttribute("score", score);
            model.addAttribute("uncorrectedWords", uncorrectedWords);
            model.addAttribute("wordSetId", id);
            LOGGER.info("Returning flashCardsResult for wordSetId: {}", id);
            return "flashCardsResult";
        } else {
            model.addAttribute("wordSetId", id);
            model.addAttribute("words", words);
            model.addAttribute("nextWordIndex", nextWordIndex);
            LOGGER.info("Returning learnFlashCards for wordSetId: {}", id);
            return "learnFlashCards";
        }
    }

    @PostMapping("/wordSet/{id}/flashCards/like")
    public String likeWord(@PathVariable long id, long wordId, Model model) {
        LOGGER.info("Entering likeWord with wordSetId: {} and wordId: {}", id, wordId);
        wordService.getWordById(wordId).addPoint();
        score++;
        nextWordIndex++;
        LOGGER.info("Word liked with wordId: {}", wordId);
        return "redirect:/wordSet/" + id + "/flashCards";
    }

    @PostMapping("/wordSet/{id}/flashCards/dislike")
    public String dislikeWord(@PathVariable long id, long wordId, Model model) {
        LOGGER.info("Entering dislikeWord with wordSetId: {} and wordId: {}", id, wordId);
        wordService.getWordById(wordId).subtractPoint();
        uncorrectedWords.add(wordService.getWordById(wordId));
        nextWordIndex++;
        LOGGER.info("Word disliked with wordId: {}", wordId);
        return "redirect:/wordSet/" + id + "/flashCards";
    }

    @GetMapping("/wordSet/{id}/test")
    public String learnTest(@PathVariable long id, Model model) {
        LOGGER.info("Entering learnTest with wordSetId: {}", id);
        List<Word> words = wordSetService.getWordsByWordSetId(id);
        if(words.size() < 4) {
            model.addAttribute("error", "Word set must have at least 4 words to start learning");
            LOGGER.warn("Word set with id: {} has less than 4 words", id);
            return "error";
        } else {
            Collections.shuffle(words);
            model.addAttribute("wordSetId", id);
            model.addAttribute("words", words);
            LOGGER.info("Returning learnTest for wordSetId: {}", id);
            return "learnTest";
        }
    }

    @PostMapping("/wordSet/{id}/submitTest")
    public String submitTest(@PathVariable long id, HttpServletRequest request, Model model) {
        LOGGER.info("Entering submitTest with wordSetId: {}", id);
        List<Word> words = wordSetService.getWordsByWordSetId(id);

        List<Word> uncorrectedWords = new ArrayList<>();
        List<String> uncorrectUserAnswers = new ArrayList<>();

        List<Word> correctWords = new ArrayList<>();

        int score = 0;

        for (Word word : words) {
            String correctAnswer = word.getTranslation();
            String userAnswer = request.getParameter("answer_" + word.getId());
            if (userAnswer != null) {
                if (userAnswer.trim().equalsIgnoreCase(correctAnswer.trim())) {
                    score++;
                    word.addPoint();
                    correctWords.add(word);
                } else {
                    word.subtractPoint();
                    uncorrectedWords.add(word);
                    uncorrectUserAnswers.add(userAnswer);
                }
                wordService.saveWord(word);
            }
        }

        words.clear();
        model.addAttribute("score", score);
        model.addAttribute("uncorrectedUserAnswers", uncorrectUserAnswers);
        model.addAttribute("uncorrectedWords", uncorrectedWords);
        model.addAttribute("wordSetId", id);

        model.addAttribute("correctWords", correctWords);
        LOGGER.info("Returning testResult for wordSetId: {}", id);
        return "testResult";
    }

}
