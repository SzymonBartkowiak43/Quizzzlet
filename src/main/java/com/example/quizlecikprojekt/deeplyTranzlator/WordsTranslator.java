package com.example.quizlecikprojekt.deeplyTranzlator;
import com.deepl.api.*;

public class WordsTranslator {
    String AUTH_KEY = "8ec376ab-7847-48d6-9941-23856ca6a578:fx";
    private static Translator translator;

    public WordsTranslator() {
        translator = new Translator(AUTH_KEY);
    }

    public String translate(String text, String targetLang) {
        try {
            return String.valueOf(translator.translateText(text, null, targetLang));
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}
