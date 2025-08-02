package com.example.quizlecikprojekt.deeplytranzlator;

import com.deepl.api.TextResult;
import com.deepl.api.Translator;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WordsTranslator {

  private static final Logger logger = LoggerFactory.getLogger(WordsTranslator.class);
  private static final int MAX_TEXT_LENGTH = 100;

  @Value("${deepl.auth-key}")
  private String authKey;

  private Translator translator;

  @PostConstruct
  public void initializeTranslator() {
    try {
      translator = new Translator(authKey);
      logger.info("DeepL Translator initialized successfully");
    } catch (Exception e) {
      logger.error("Failed to initialize DeepL Translator", e);
    }
  }

  public String translate(String text, String sourceLanguage, String targetLanguage) {
    try {
      if (translator == null) {
        logger.warn("Translator not initialized");
        return text;
      }

      if (text == null || text.trim().isEmpty()) {
        return text;
      }

      if (text.length() > MAX_TEXT_LENGTH) {
        logger.warn("Text too long for translation: {} characters", text.length());
        return text;
      }

      TextResult result = translator.translateText(text.trim(), sourceLanguage, targetLanguage);
      String translatedText = result.getText();

      logger.debug("Translation: '{}' -> '{}'", text, translatedText);
      return translatedText;

    } catch (Exception e) {
      logger.error("Translation failed for text: '{}'", text, e);
      return text;
    }
  }
}
