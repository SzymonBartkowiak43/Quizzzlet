package com.example.quizlecikprojekt.newweb.dto.word;

import java.util.Date;

public record WordResponse(
        Long id,
        String word,
        String translation,
        int points,
        Date lastPracticed
) {
}