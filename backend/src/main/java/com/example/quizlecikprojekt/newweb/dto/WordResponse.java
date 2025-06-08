package com.example.quizlecikprojekt.newweb.dto;

import java.util.Date;

public class WordResponse {
    private Long id;
    private String word;
    private String translation;
    private int points;
    private Date lastPracticed;

    public WordResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }

    public String getTranslation() { return translation; }
    public void setTranslation(String translation) { this.translation = translation; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public Date getLastPracticed() { return lastPracticed; }
    public void setLastPracticed(Date lastPracticed) { this.lastPracticed = lastPracticed; }
}