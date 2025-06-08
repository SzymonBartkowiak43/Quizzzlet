package com.example.quizlecikprojekt.newweb.dto;

import java.util.List;

public class TestStartResponse {
    private Long wordSetId;
    private List<WordResponse> words;
    private int totalQuestions;

    public TestStartResponse() {}

    public Long getWordSetId() { return wordSetId; }
    public void setWordSetId(Long wordSetId) { this.wordSetId = wordSetId; }

    public List<WordResponse> getWords() { return words; }
    public void setWords(List<WordResponse> words) { this.words = words; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
}