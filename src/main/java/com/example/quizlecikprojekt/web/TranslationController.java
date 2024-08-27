package com.example.quizlecikprojekt.web;

import com.example.quizlecikprojekt.deeplyTranzlator.WordsTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TranslationController {
    private final WordsTranslator wordsTranslator;
    private final static Logger LOGGER = LoggerFactory.getLogger(TranslationController.class);

    public TranslationController(WordsTranslator wordsTranslator) {
        this.wordsTranslator = wordsTranslator;
    }

    @GetMapping("/translate")
    public String translate(@RequestParam String text, @RequestParam String currentLanguage, @RequestParam String targetLang) {
        LOGGER.info("Entering translate with text: {}, currentLanguage: {}, targetLang: {}", text, currentLanguage, targetLang);
        String translation = wordsTranslator.translate(text, currentLanguage, targetLang);
        if (translation.equals(text)) {
            LOGGER.info("Translation is the same as the original text");
            return "";
        } else {
            LOGGER.info("Translation successful");
            return translation;
        }
    }
}