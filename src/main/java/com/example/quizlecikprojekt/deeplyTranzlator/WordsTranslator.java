package com.example.quizlecikprojekt.deeplyTranzlator;
import com.deepl.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WordsTranslator {
    String AUTH_KEY = "8ec376ab-7847-48d6-9941-23856ca6a578:fx";
    private static Translator translator;
    private final static Logger LOGGER = LoggerFactory.getLogger(WordsTranslator.class);

    public WordsTranslator() {
        translator = new Translator(AUTH_KEY);
    }

    public String translate(String text, String currentLanguage, String targetLang) {
        LOGGER.info("Entering translate word: {}, currentLanguage: {}, targetLang: {}", text, currentLanguage, targetLang);
        try {
            if (text.length() > 100) {
                LOGGER.warn("Text length exceeds 100 characters");
                return "error";
            } else {
                TextResult result = translator.translateText(text, currentLanguage, targetLang);
                LOGGER.info("Translation successful: {}", result.getText());
                return result.getText();
            }
        } catch (Exception e) {
            LOGGER.error("Translation failed", e);
            return "error";
        }
    }
}
