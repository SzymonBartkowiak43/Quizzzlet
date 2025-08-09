package com.example.quizlecikprojekt.controllers.dto.word;

import java.util.Date;

public record WordResponse(
    Long id,
    String word,
    String translation,
    Integer points,
    boolean star,
    Date lastPracticed,
    Long wordSetId) {}
