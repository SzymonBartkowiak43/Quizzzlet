package com.example.quizlecikprojekt.newweb.dto.review;

public record ReviewStatusResponse (
   int correctWordCount,
    int totalWords,
     String sessionId
){}