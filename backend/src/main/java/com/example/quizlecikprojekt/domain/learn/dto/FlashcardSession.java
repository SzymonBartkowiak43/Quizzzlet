package com.example.quizlecikprojekt.domain.learn.dto;

import com.example.quizlecikprojekt.entity.Word;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class FlashcardSession {
  private String sessionId;
  private Long wordSetId;
  private String wordSetTitle;
  private List<Word> words;
  private int currentIndex;
  private int score;
  private List<Word> incorrectWords;
  private LocalDateTime startedAt;


  public void incrementScore() {
    this.score++;
  }

  public void setIncorrectWords(List<Word> incorrectWords) {
    this.incorrectWords = incorrectWords;
  }

  public void addIncorrectWord(Word word) {
    this.incorrectWords.add(word);
  }

}
