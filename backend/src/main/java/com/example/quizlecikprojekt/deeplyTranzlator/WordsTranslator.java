package com.example.quizlecikprojekt.deeplyTranzlator;

import com.deepl.api.TextResult;
import com.deepl.api.Translator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WordsTranslator {
    private static Translator translator;
    @Value("${deepl.auth-key}")
    private String authKey;


    public String translate(String text, String currentLanguage, String targetLang) {
        try {
            if (translator == null) {
                translator = new Translator(authKey);
            }
            if (text.length() > 100) {
                return "error";
            } else {
                TextResult result = translator.translateText(text, currentLanguage, targetLang);
                return result.getText();
            }
        } catch (Exception e) {
            return "error";
        }
    }
}
