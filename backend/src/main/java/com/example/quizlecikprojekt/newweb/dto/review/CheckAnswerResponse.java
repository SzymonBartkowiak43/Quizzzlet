package com.example.quizlecikprojekt.newweb.dto.review;


public class CheckAnswerResponse {
    private boolean userCorrect;
    private boolean wordCorrect;
    private int correctWordCount;
    private int totalWords;

    public CheckAnswerResponse() {}

    public CheckAnswerResponse(boolean userCorrect, boolean wordCorrect, int correctWordCount, int totalWords) {
        this.userCorrect = userCorrect;
        this.wordCorrect = wordCorrect;
        this.correctWordCount = correctWordCount;
        this.totalWords = totalWords;
    }

    // Getters and setters
    public boolean isUserCorrect() { return userCorrect; }
    public void setUserCorrect(boolean userCorrect) { this.userCorrect = userCorrect; }

    public boolean isWordCorrect() { return wordCorrect; }
    public void setWordCorrect(boolean wordCorrect) { this.wordCorrect = wordCorrect; }

    public int getCorrectWordCount() { return correctWordCount; }
    public void setCorrectWordCount(int correctWordCount) { this.correctWordCount = correctWordCount; }

    public int getTotalWords() { return totalWords; }
    public void setTotalWords(int totalWords) { this.totalWords = totalWords; }
}