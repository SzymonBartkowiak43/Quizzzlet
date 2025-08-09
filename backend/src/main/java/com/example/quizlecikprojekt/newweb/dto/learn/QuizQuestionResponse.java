package com.example.quizlecikprojekt.newweb.dto.learn;

import java.util.List;

public record QuizQuestionResponse(
        Long wordId,
        String question,
        List<String> options,
        String questionType // "word_to_translation" or "translation_to_word"
) {}