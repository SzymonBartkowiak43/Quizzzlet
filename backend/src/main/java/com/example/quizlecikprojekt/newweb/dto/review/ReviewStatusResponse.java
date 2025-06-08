package com.example.quizlecikprojekt.newweb.dto.review;

public class ReviewStatusResponse {
    private int correctWordCount;
    private int totalWords;
    private String sessionId;

    public ReviewStatusResponse() {}

    public ReviewStatusResponse(int correctWordCount, int totalWords, String sessionId) {
        this.correctWordCount = correctWordCount;
        this.totalWords = totalWords;
        this.sessionId = sessionId;
    }

    // Getters and setters
    public int getCorrectWordCount() { return correctWordCount; }
    public void setCorrectWordCount(int correctWordCount) { this.correctWordCount = correctWordCount; }

    public int getTotalWords() { return totalWords; }
    public void setTotalWords(int totalWords) { this.totalWords = totalWords; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
}