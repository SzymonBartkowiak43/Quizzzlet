package com.example.quizlecikprojekt.newweb.dto;

import java.util.List;

public class FlashCardAnswerResponse {
    private int score;
    private int currentIndex;
    private int totalWords;
    private boolean completed;
    private WordResponse nextWord;
    private List<WordResponse> uncorrectedWords;

    // constructors, getters, setters
    public FlashCardAnswerResponse() {}

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getCurrentIndex() { return currentIndex; }
    public void setCurrentIndex(int currentIndex) { this.currentIndex = currentIndex; }

    public int getTotalWords() { return totalWords; }
    public void setTotalWords(int totalWords) { this.totalWords = totalWords; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public WordResponse getNextWord() { return nextWord; }
    public void setNextWord(WordResponse nextWord) { this.nextWord = nextWord; }

    public List<WordResponse> getUncorrectedWords() { return uncorrectedWords; }
    public void setUncorrectedWords(List<WordResponse> uncorrectedWords) { this.uncorrectedWords = uncorrectedWords; }
}
