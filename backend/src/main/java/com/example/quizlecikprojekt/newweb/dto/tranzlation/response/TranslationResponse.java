package com.example.quizlecikprojekt.newweb.dto.tranzlation.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TranslationResponse {
  private String originalText;
  private String translatedText;
  private String sourceLanguage;
  private String targetLanguage;
  private boolean translationSuccessful;
  private String message;
}
