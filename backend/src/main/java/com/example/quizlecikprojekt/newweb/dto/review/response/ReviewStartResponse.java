package com.example.quizlecikprojekt.newweb.dto.review.response;

import java.util.List;

public record ReviewStartResponse(
        String sessionId,
        int totalWords,
        int correctWordCount,
        List<WordToRepeatResponse> initialWords,
        WordToRepeatResponse currentWord
) {
}