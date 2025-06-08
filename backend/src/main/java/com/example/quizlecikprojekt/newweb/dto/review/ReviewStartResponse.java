package com.example.quizlecikprojekt.newweb.dto.review;

import java.util.List;

public class ReviewStartResponse {
    private String sessionId;
    private int totalWords;
    private int correctWordCount;
    private List<WordToRepeadResponse> initialWords;
    private WordToRepeadResponse currentWord;

    public ReviewStartResponse() {}

    public ReviewStartResponse(String sessionId, int totalWords, int correctWordCount,
                               List<WordToRepeadResponse> initialWords, WordToRepeadResponse currentWord) {
        this.sessionId = sessionId;
        this.totalWords = totalWords;
        this.correctWordCount = correctWordCount;
        this.initialWords = initialWords;
        this.currentWord = currentWord;
    }

    // Getters and setters
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public int getTotalWords() { return totalWords; }
    public void setTotalWords(int totalWords) { this.totalWords = totalWords; }

    public int getCorrectWordCount() { return correctWordCount; }
    public void setCorrectWordCount(int correctWordCount) { this.correctWordCount = correctWordCount; }

    public List<WordToRepeadResponse> getInitialWords() { return initialWords; }
    public void setInitialWords(List<WordToRepeadResponse> initialWords) { this.initialWords = initialWords; }

    public WordToRepeadResponse getCurrentWord() { return currentWord; }
    public void setCurrentWord(WordToRepeadResponse currentWord) { this.currentWord = currentWord; }
}