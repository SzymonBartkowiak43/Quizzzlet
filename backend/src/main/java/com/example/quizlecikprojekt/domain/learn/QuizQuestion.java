package com.example.quizlecikprojekt.domain.learn;

import com.example.quizlecikprojekt.domain.word.Word;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuizQuestion {
  private Word word;
  private String question;
  private String correctAnswer;
  private List<String> options;
  private String questionType;
}
