package com.example.quizlecikprojekt.newweb.dto.review;

public class NextWordResponse {
    private WordToRepeadResponse word;
    private int correctWordCount;
    private int totalWords;

    public NextWordResponse() {}

    public NextWordResponse(WordToRepeadResponse word, int correctWordCount, int totalWords) {
        this.word = word;
        this.correctWordCount = correctWordCount;
        this.totalWords = totalWords;
    }

    // Getters and setters
    public WordToRepeadResponse getWord() { return word; }
    public void setWord(WordToRepeadResponse word) { this.word = word; }

    public int getCorrectWordCount() { return correctWordCount; }
    public void setCorrectWordCount(int correctWordCount) { this.correctWordCount = correctWordCount; }

    public int getTotalWords() { return totalWords; }
    public void setTotalWords(int totalWords) { this.totalWords = totalWords; }
}