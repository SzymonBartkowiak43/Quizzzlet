package com.example.quizlecikprojekt.newweb.dto;

public class FlashCardStartResponse {
    private String sessionId;
    private int totalWords;
    private WordResponse currentWord;
    private int currentIndex;

    // constructors, getters, setters
    public FlashCardStartResponse() {}

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public int getTotalWords() { return totalWords; }
    public void setTotalWords(int totalWords) { this.totalWords = totalWords; }

    public WordResponse getCurrentWord() { return currentWord; }
    public void setCurrentWord(WordResponse currentWord) { this.currentWord = currentWord; }

    public int getCurrentIndex() { return currentIndex; }
    public void setCurrentIndex(int currentIndex) { this.currentIndex = currentIndex; }
}