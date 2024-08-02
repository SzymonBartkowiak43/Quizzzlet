package com.example.quizlecikprojekt.deeplyTranzlator;
import com.deepl.api.*;
import org.springframework.stereotype.Service;

@Service
public class WordsTranslator {
    String AUTH_KEY = "8ec376ab-7847-48d6-9941-23856ca6a578:fx";
    private static Translator translator;

    public WordsTranslator() {
        translator = new Translator(AUTH_KEY);
    }

    public String translate(String text,String currentLanguage, String targetLang) {
        try {
            if(text.length() > 100) {
                return "error";
            } else {
                TextResult result = translator.translateText(text, currentLanguage, targetLang);
                System.out.println(text + " " + targetLang + " result: " + result.getText() + text.length());
                return result.getText();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}
