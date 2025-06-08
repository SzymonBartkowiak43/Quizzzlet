package com.example.quizlecikprojekt.newweb.dto;

public class TestAnswerResult {
    private Long wordId;
    private String word;
    private String correctAnswer;
    private String userAnswer;
    private boolean correct;

    public TestAnswerResult() {}

    public Long getWordId() { return wordId; }
    public void setWordId(Long wordId) { this.wordId = wordId; }

    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public String getUserAnswer() { return userAnswer; }
    public void setUserAnswer(String userAnswer) { this.userAnswer = userAnswer; }

    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }
}