package com.example.quizlecikprojekt.word;


import com.example.quizlecikprojekt.wordSet.WordSet;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String word;
    private String translation;

    @ManyToOne
    @JoinColumn(name = "word_set_id", nullable = false)
    private WordSet wordSet;



}
