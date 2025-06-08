package com.example.quizlecikprojekt.newweb.dto.review;

public class WordToRepeadResponse {
    private Long id;
    private String word;
    private String translation;
    private boolean correct;

    public WordToRepeadResponse() {}

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }

    public String getTranslation() { return translation; }
    public void setTranslation(String translation) { this.translation = translation; }

    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }
}