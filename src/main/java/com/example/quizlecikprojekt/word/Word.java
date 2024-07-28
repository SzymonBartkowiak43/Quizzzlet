package com.example.quizlecikprojekt.word;


import com.example.quizlecikprojekt.wordSet.WordSet;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@Entity
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String word;
    private String translation;
    private Integer points;
    private boolean star;
    private Date lastPracticed;

    @ManyToOne
    @JoinColumn(name = "word_set_id", nullable = false)
    private WordSet wordSet;

    public void addPoint() {
        lastPracticed = new Date(System.currentTimeMillis());
        if(points < 2) {
            points++;
        }
    }

    public void subtractPoint() {
        lastPracticed = new Date(System.currentTimeMillis());
        if(points > 0) {
            points--;
        }
    }



}
