package com.example.quizlecikprojekt.deeplyTranzlator;
import com.deepl.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WordsTranslator {
    private static Translator translator;
    private final static Logger LOGGER = LoggerFactory.getLogger(WordsTranslator.class);

    @Value("${deepl.auth-key}")
    private String authKey;

    public WordsTranslator() {
    }

    public String translate(String text, String currentLanguage, String targetLang) {
        LOGGER.info("Entering translate word: {}, currentLanguage: {}, targetLang: {}", text, currentLanguage, targetLang);
        try {
            if (translator == null) {
                translator = new Translator(authKey);
            }
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
