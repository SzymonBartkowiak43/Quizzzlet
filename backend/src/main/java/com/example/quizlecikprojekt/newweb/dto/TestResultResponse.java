package com.example.quizlecikprojekt.newweb.dto;


import java.util.List;

public class TestResultResponse {
    private int score;
    private int totalQuestions;
    private double percentage;
    private List<TestAnswerResult> results;
    private List<WordResponse> correctWords;
    private List<WordResponse> incorrectWords;

    public TestResultResponse() {}

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }

    public List<TestAnswerResult> getResults() { return results; }
    public void setResults(List<TestAnswerResult> results) { this.results = results; }

    public List<WordResponse> getCorrectWords() { return correctWords; }
    public void setCorrectWords(List<WordResponse> correctWords) { this.correctWords = correctWords; }

    public List<WordResponse> getIncorrectWords() { return incorrectWords; }
    public void setIncorrectWords(List<WordResponse> incorrectWords) { this.incorrectWords = incorrectWords; }
}